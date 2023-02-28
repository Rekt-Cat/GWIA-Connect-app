package com.maid.gwiaconnect;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Models.NewsModel;
import adapters.FeedRecyclerAdapter;
import adapters.NewsRecyclerAdapter;


public class news_fragment extends Fragment {
    ExpandableTextView expandableTextView;
    RecyclerView recyclerView;
    ArrayList<NewsModel> arr;
    ProgressBar progressBar;
    ArrayList<String> des;
    ArrayList<String> date;
    ArrayList<String> cat;
    ArrayList<String> title;
    Exception exception = null;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getLayoutInflater().inflate(R.layout.news_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cat = new ArrayList<>();
        des = new ArrayList<>();
        date = new ArrayList<>();
        arr = new ArrayList<>();
        title=new ArrayList<>();

        ExecutorService service = Executors.newSingleThreadExecutor();


        service.execute(new Runnable() {

            @Override
            public void run() {
                // this for initiating variables and show progress bar
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressBar = view.findViewById(R.id.loading);

                        progressBar.setVisibility(View.VISIBLE);

                    }
                });

                //this space is for all the heavy task

                try {
                    URL url = new URL("https://rss.feedspot.com/folder/5BbEuGci5A==/rss/rsscombiner");
                    recyclerView = view.findViewById(R.id.newsRecycler);




                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(url.openConnection().getInputStream(), "UTF_8");
                    boolean insideItem = false;

                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {

                        if (eventType == XmlPullParser.START_TAG) {



                            if (xpp.getName().equalsIgnoreCase("item")) {
                                insideItem = true;
                            }
                            else if (xpp.getName().equalsIgnoreCase("title")) {
                                if (insideItem) {
                                    cat.add(xpp.nextText());

                                }
                            }
                            else if (xpp.getName().equalsIgnoreCase("description")) {
                                if (insideItem) {
                                    des.add(Html.fromHtml(xpp.nextText()).toString());

                                }
                            }
                            else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                                if (insideItem) {
                                    date.add(xpp.nextText());

                                }
                            }
                        }
                        else if(eventType==XmlPullParser.END_TAG&&xpp.getName().equalsIgnoreCase("item")){
                            insideItem=false;
                        }
                        eventType =xpp.next();
                    }
                    for (int i = 0; i < cat.size(); i++) {
                        arr.add(new NewsModel("World",cat.get(i),date.get(i),des.get(i)));
                        Log.d("lol",cat.get(i)+","+des.get(i)+"+"+date.get(i));
                    }
//                    for (int i = 0; i < cat.size(); i++) {
//                        Log.d("poop",String.valueOf(arr.get(i)));
//                        //Log.d("lol",cat.get(i)+","+des.get(i)+"+"+date.get(i));
//                    }




                } catch (MalformedURLException e) {
                    exception = e;
                } catch (XmlPullParserException e) {
                    exception = e;
                } catch (IOException e) {
                    exception = e;

                }


                //this is to communicate with the ui
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        Toast toast=Toast. makeText(getContext(),"The exceeption is : "+exception,Toast. LENGTH_LONG);
//                        toast.show();
                        NewsRecyclerAdapter newsRecyclerAdapter = new NewsRecyclerAdapter(arr, getContext());
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.getLayoutManager().setMeasurementCacheEnabled(false);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(newsRecyclerAdapter);

                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });


            }


        });




    }



}
