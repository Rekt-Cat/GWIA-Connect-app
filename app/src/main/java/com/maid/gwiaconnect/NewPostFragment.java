package com.maid.gwiaconnect;

import static android.app.Activity.RESULT_OK;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class NewPostFragment extends Fragment {

    Uri imageUri;
    String myUrl="";
    StorageTask uploadTask;
    StorageReference storageReference;
    ImageView postImage;
    EditText description;
    Button post,selectImage;
    ActivityResultLauncher<String> mGetContent;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_post, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postImage= view.findViewById(R.id.postImage);
        description=view.findViewById(R.id.des);
        post=view.findViewById(R.id.post);
        selectImage=view.findViewById(R.id.selectImage);
        storageReference = FirebaseStorage.getInstance().getReference("posts");
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });
        mGetContent=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent i = new Intent(getContext(),CropperActivity.class);
                if(result.toString()!=null) {
                    i.putExtra("DATA", result.toString());
                    startActivityForResult(i, 101);
                }
                else{

                }
            }
        });





    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        Log.d(TAG, "getFileExtension: "+mime.getExtensionFromMimeType(contentResolver.getType(uri)));
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        ProgressDialog pd= new ProgressDialog(getContext());
        pd.setMessage("Posting...");
        pd.show();

        if(imageUri!=null){
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask=fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        myUrl=downloadUri.toString();
                        Log.d("urlo", "onComplete: "+ myUrl);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
                        String postId= reference.push().getKey();
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("PostId",postId);
                        hashMap.put("Description",description.getText().toString());
                        hashMap.put("PostImage",myUrl);
                        hashMap.put("Publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.child(postId).setValue(hashMap);
                        pd.dismiss();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContain, new feed_fragment()).commit();

                    }
                    else{
                        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                       }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("lol", "onComplete: "+e.getMessage());
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            Toast.makeText(getContext(), "Please select an image to post!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==-1&&requestCode==101){
            String result = data.getStringExtra("RESULT");
            Uri resultUri=null;
            if(result!=null){
                imageUri=Uri.parse(result);
            }
            postImage.setImageURI(imageUri);

        }
    }
}