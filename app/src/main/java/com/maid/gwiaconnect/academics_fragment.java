package com.maid.gwiaconnect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Models.AcademicsModel;
import Models.AcademicsModel2;
import adapters.AcademicsRecyclerAdapter;
import adapters.AcademicsRecyclerAdapter2;

public class academics_fragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    ArrayList<AcademicsModel> arr;
    ArrayList<AcademicsModel2> arr2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getLayoutInflater().inflate(R.layout.academic_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arr=new ArrayList<>();
        arr.add(new AcademicsModel("9am-10:30am","Communication"));
        arr.add(new AcademicsModel("12pm-1:30pm","English"));
        recyclerView=view.findViewById(R.id.timeRecycler);
        AcademicsRecyclerAdapter academicsRecyclerAdapter = new AcademicsRecyclerAdapter(arr,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(academicsRecyclerAdapter);


        recyclerView2= view.findViewById(R.id.attendance);
        arr2= new ArrayList<>();
        arr2.add(new AcademicsModel2("Communication",50));
        arr2.add(new AcademicsModel2("English",75));
        AcademicsRecyclerAdapter2 academicsRecyclerAdapter2 = new AcademicsRecyclerAdapter2(arr2,getContext());
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setAdapter(academicsRecyclerAdapter2);
    }
}
