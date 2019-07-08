package com.bytedance.android.lesson.restapi.solution;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bytedance.android.lesson.restapi.solution.bean.Feed;
import com.bytedance.android.lesson.restapi.solution.bean.FeedResponse;
import com.bytedance.android.lesson.restapi.solution.bean.PostVideoResponse;
import com.bytedance.android.lesson.restapi.solution.newtork.IMiniDouyinService;
import com.bytedance.android.lesson.restapi.solution.utils.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Part;
import retrofit2.http.Query;

public class Solution2C2Activity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final String TAG = "Solution2C2Activity";
    private RecyclerView mRv;
    private List<Feed> mFeeds = new ArrayList<>();
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    public Button mBtn;
    private Button mBtnRefresh;
    private Button rBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution2_c2);
        initRecyclerView();
        initBtns();


        ActivityCompat.requestPermissions(Solution2C2Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);


    }
    private void initBtns() {

        mBtn = findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String s = mBtn.getText().toString();
                System.out.println(s);
                if (getString(R.string.select_an_image).equals(s)) {
                    chooseImage();
                } else if (getString(R.string.select_a_video).equals(s)) {
                    chooseVideo();
                } else if (getString(R.string.post_it).equals(s)) {
                    if (mSelectedVideo != null && mSelectedImage != null) {
                        postVideo();
                    } else {
                        throw new IllegalArgumentException("error data uri, mSelectedVideo = " + mSelectedVideo + ", mSelectedImage = " + mSelectedImage);
                    }
                } else if ((getString(R.string.success_try_refresh).equals(s))) {
                    mBtn.setText(R.string.select_an_image);
                }
            }
        });

        mBtnRefresh = findViewById(R.id.btn_refresh);
        rBtn = findViewById(R.id.btn_record);
        rBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Solution2C2Activity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //todo 在这里申请相机、存储的权限
                    ActivityCompat.requestPermissions(Solution2C2Activity.this, new String[]{Manifest.permission.CAMERA}, 1);
                }
                else {
                    Intent intent_r = new Intent(Solution2C2Activity.this, CustomCameraActivity.class);
                    startActivity(intent_r);
                }
            }
        });



    }

    private void initRecyclerView() {
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(new RecyclerView.Adapter() {
            @NonNull @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                ImageView imageView = new ImageView(viewGroup.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                return new Solution2C1Activity.MyViewHolder(imageView);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                ImageView iv = (ImageView) viewHolder.itemView;

                // TODO-C2 (10) Uncomment these 2 lines, assign image url of Feed to this url variable

                String url = mFeeds.get(i).urlback();
                Glide.with(iv.getContext()).load(url).into(iv);
                final String video_url = mFeeds.get(i).getVideo_url();
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), IJKPlayerActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("url",video_url);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }

            @Override public int getItemCount() {
                return mFeeds.size();
            }
        });
    }

    public void chooseImage() {
        // TODO-C2 (4) Start Activity to select an image
        System.out.println("开搞image");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select picture"),PICK_IMAGE);
    }


    public void chooseVideo() {
        // TODO-C2 (5) Start Activity to select a video

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select video"),PICK_VIDEO);
        System.out.println("搞完video");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (resultCode == RESULT_OK && null != data) {

            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.d(TAG, "selectedImage = " + mSelectedImage);
                mBtn.setText(R.string.select_a_video);
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
                mBtn.setText(R.string.post_it);
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        grantUriPermission(getPackageName(),mSelectedImage,Intent.FLAG_GRANT_READ_URI_PERMISSION);
        File f = new File(ResourceUtils.getRealPath(Solution2C2Activity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo() {
        mBtn.setText("POSTING...");
        mBtn.setEnabled(false);

        // TODO-C2 (6) Send Request to post a video with its cover image
        // if success, make a text Toast and show
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://test.androidcamp.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IMiniDouyinService req = retrofit.create(IMiniDouyinService.class);

//        List<MultipartBody.Part> parts;
//        parts.add(getMultipartFromUri("image_url",mSelectedImage));
//        parts.add(getMultipartFromUri("image_url",mSelectedVideo));

//        Feed feed = new Feed();
//        feed.setImage_url(mSelectedImage.toString());
//        feed.setVideo_url(mSelectedVideo.toString());
        String student_id = "16061015";
        String user_name = "zhouhaici";
//        feed.setStudent_id(student_id);+
//        feed.setUser_name(user_name);

        Call<PostVideoResponse> call = req.request(getMultipartFromUri("cover_image",mSelectedImage),getMultipartFromUri("video",mSelectedVideo), student_id,user_name);
        //怎么传这俩破玩意？

        call.enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                System.out.println("成功");
                Toast.makeText(Solution2C2Activity.this,"成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable throwable) {
                System.out.println("失败");
                Toast.makeText(Solution2C2Activity.this,"失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchFeed(View view) {
        mBtnRefresh.setText("requesting...");
        mBtnRefresh.setEnabled(false);

        // TODO-C2 (9) Send Request to fetch feed
        // if success, assign data to mFeeds and call mRv.getAdapter().notifyDataSetChanged()
        // don't forget to call resetRefreshBtn() after response received
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://test.androidcamp.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IMiniDouyinService req = retrofit.create(IMiniDouyinService.class);
        Call<FeedResponse> call = req.getFeed();
        Toast.makeText(Solution2C2Activity.this,"fetch",Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                Toast.makeText(Solution2C2Activity.this,"刷新成功",Toast.LENGTH_SHORT).show();
                mFeeds = response.body().listback();
                mRv.getAdapter().notifyDataSetChanged();
                resetRefreshBtn();
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable throwable) {
                Toast.makeText(Solution2C2Activity.this,"暂无内容",Toast.LENGTH_SHORT).show();
                resetRefreshBtn();
            }
        });


    }

    private void resetRefreshBtn() {
        mBtnRefresh.setText(R.string.refresh_feed);
        mBtnRefresh.setEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                //todo 判断权限是否已经授予
                ActivityCompat.requestPermissions(Solution2C2Activity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1010);
                break;
            }
        }
    }

}
