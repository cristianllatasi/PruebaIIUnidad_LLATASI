package com.example.pruebaiiunidad_llatasi;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private ProgressDialog mProgressDialog;
    private String url = "https://www.yudiz.com/blog/";
    private ArrayList<String> mAuthorNameList = new ArrayList<>();
    private ArrayList<String> mBlogUploadDateList = new ArrayList<>();
    private ArrayList<String> mPaginationList = new ArrayList<>();
    private ArrayList<String> mBlogTitleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Description().execute();

    }


    private class Description extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Android Basic JSoup Tutorial");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document mBlogDocument = Jsoup.connect(url).get();

                int mPaginationSize = mBlogDocument.select("div[class=pages]").select("a").size();

                for (int page = 0; page < mPaginationSize; page++) {

                    Elements mPageLinkTaga = mBlogDocument.select("div.pages a").eq(page);
                    String mPageLink = mPageLinkTaga.attr("href");

                    mPaginationList.add(mPageLink);
                    Log.i("TAG1", mPageLink);
                }

                for (int j = 0; j < mPaginationList.size(); j++) {
                    Document mBlogPagination = Jsoup.connect(mPaginationList.get(j)).get();

                    // Using Elements to get the Meta data
                    Elements mElementDataSize = mBlogPagination.select("div[class=author-date]");
                    // Locate the content attribute
                    int mElementSize = mElementDataSize.size();

                    for (int i = 0; i < mElementSize; i++) {
                        Elements mElementAuthorName = mBlogPagination.select("span[class=vcard author post-author test]").select("a").eq(i);
                        String mAuthorName = mElementAuthorName.text().trim().replace("\n", "").replace("\t", "").replace("\r", "").replace("\b", "");

                        Elements mElementBlogUploadDate = mBlogPagination.select("span[class=post-date updated]").eq(i);
                        String mBlogUploadDate = mElementBlogUploadDate.text();

                        Elements mElementBlogTitle = mBlogPagination.select("h2[class=entry-title]").select("a").eq(i);
                        String mBlogTitle = mElementBlogTitle.text();

                        mAuthorNameList.add(mAuthorName);
                        mBlogUploadDateList.add(mBlogUploadDate);
                        mBlogTitleList.add(mBlogTitle);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // Set description into TextView

            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.act_recyclerview);

            DataAdapter mDataAdapter = new DataAdapter(MainActivity.this, mBlogTitleList, mAuthorNameList, mBlogUploadDateList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mDataAdapter);

            mProgressDialog.dismiss();
        }
    }
}

