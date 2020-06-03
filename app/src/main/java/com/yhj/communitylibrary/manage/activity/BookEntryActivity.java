package com.yhj.communitylibrary.manage.activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hacknife.immersive.Immersive;
import com.yhj.communitylibrary.HomeActivity;
import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.manage.Book;
import com.yhj.communitylibrary.utils.DownJsonUtils;
import com.yhj.communitylibrary.utils.UrlPath;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * author : yhj
 * date  :2019/12/3
 * desc   :图书录入页面
 */
public class BookEntryActivity extends AppCompatActivity {

    @BindView(R.id.book_et_title)
    EditText bookEtTitle;
    @BindView(R.id.book_et_author)
    EditText bookEtAuthor;
    @BindView(R.id.book_et_isbn)
    EditText bookEtIsbn;
    @BindView(R.id.book_et_publisher)
    EditText bookEtPublisher;
    @BindView(R.id.book_et_pages)
    EditText bookEtPages;
    @BindView(R.id.book_et_pubdate)
    EditText bookEtPubdate;
    @BindView(R.id.book_et_price)
    EditText bookEtPrice;
    @BindView(R.id.book_sp_category)
    Spinner bookSpCategory;
    @BindView(R.id.book_et_description)
    EditText bookEtDescription;
    @BindView(R.id.book_iv_cover)
    ImageView bookIvCover;
    @BindView(R.id.book_bt_save)
    Button bookBtSave;
    @BindView(R.id.book_et_admin)
    EditText bookEtAdmin;
    private String img;
    private String imagePath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.setContentView(this, R.layout.activity_book_entry, R.color.myblue, R.color.black, false, false);

        ButterKnife.bind(this);


        bookEtAdmin.setText(HomeActivity.name);//用户不用输入

        initData();


    }

    private void initData() {
        bookSpCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                String[] mStringsItem = getResources().getStringArray(R.array.sping_text);
                TextView tv = (TextView) arg1;
                tv.setTextColor(Color.GRAY);
                tv.setTextSize(14f);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        Intent intent = getIntent();
        String isbn = intent.getStringExtra("ISBN");
        if (isbn != null) {
            DownJsonUtils.downJsonOKHTTP_GET(UrlPath.path_0(isbn), new DownJsonUtils.OnsendDataListener() {
                @Override
                public void onSendData(String json) throws JSONException {
                    //Log.e("yhj", "条维码" + json);
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject showapi_res_body = jsonObject.getJSONObject("showapi_res_body");
                    int ret_code = showapi_res_body.getInt("ret_code");
                    if (ret_code == 0) {
                        JSONObject data = showapi_res_body.getJSONObject("data");

                        bookEtTitle.setText(data.getString("title"));
                        bookEtAuthor.setText(data.getString("author"));
                        bookEtIsbn.setText(data.getString("isbn"));
                        bookEtPublisher.setText(data.getString("publisher"));
                        bookEtPages.setText(data.getString("page"));
                        bookEtPubdate.setText(data.getString("pubdate"));
                        bookEtPrice.setText(data.getString("price"));
                        bookEtDescription.setText(data.getString("gist"));
                        img = data.getString("img");
                        if (!img.equals("")) {
                            RequestOptions requestOptions = new RequestOptions();
                            Glide.with(BookEntryActivity.this).load(img).apply(requestOptions).into(bookIvCover);
                        }


                    } else {
                        Toast.makeText(BookEntryActivity.this, "没有找到对应的书籍，请手动输入！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    @OnClick({R.id.book_bt_save, R.id.book_iv_cover})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.book_iv_cover://选取照片
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                break;
            case R.id.book_bt_save://保存
                if (TextUtils.isEmpty(bookEtTitle.getText()) || TextUtils.isEmpty(bookEtAuthor.getText()) || TextUtils.isEmpty(bookEtIsbn.getText())
                        || TextUtils.isEmpty(bookEtPublisher.getText()) || TextUtils.isEmpty(bookEtPages.getText()) || TextUtils.isEmpty(bookEtPubdate.getText())
                        || TextUtils.isEmpty(bookEtPrice.getText()) || bookSpCategory.getSelectedItem().toString().equals("全部分类") || TextUtils.isEmpty(bookEtDescription.getText())
                        || bookIvCover.getDrawable().getCurrent().getConstantState().equals(ContextCompat.getDrawable(this, R.drawable.android).getConstantState())) {
                    Toast.makeText(this, "请完整填写图书信息！！！！", Toast.LENGTH_SHORT).show();
                } else {
                    //图书添加
                    //先判断指定读者上传此书没有
                    BmobQuery<Book> bookAdminQuery = new BmobQuery<>();
                    bookAdminQuery.addWhereEqualTo("book_admin", HomeActivity.name);
                    BmobQuery<Book> bookIsbnQuery = new BmobQuery<>();
                    bookIsbnQuery.addWhereEqualTo("isbn", bookEtIsbn.getText().toString());
                    ArrayList<BmobQuery<Book>> bmobQueries = new ArrayList<>();
                    bmobQueries.add(bookAdminQuery);
                    bmobQueries.add(bookIsbnQuery);
                    BmobQuery<Book> bookBmobQuery = new BmobQuery<>();
                    bookBmobQuery.and(bmobQueries);
                    bookBmobQuery.findObjects(new FindListener<Book>() {
                        @Override
                        public void done(List<Book> object, BmobException e) {
                            if (e == null) {
                                if (object.size() > 0) {
                                    Toast.makeText(BookEntryActivity.this, "您的此本书籍已添加！", Toast.LENGTH_SHORT).show();
                                } else {
                                        bookAdd();
                                }
                            } else {

                                Toast.makeText(BookEntryActivity.this, "获取失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

                break;
        }
    }

    private void bookAdd() {
//        Log.e("yhj", "本地地址" + imagePath);

//        Log.e("yhj", "json地址" + img);

        if (!TextUtils.isEmpty(imagePath)) {
            //TODO bmob上传文件需要绑定文件域名，需要购买域名和备案，可以取消这个判断直接调用 addIncrease(img);
            BmobFile bmobFile = new BmobFile(new File(imagePath));
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        String fileUrl = bmobFile.getFileUrl();
                        Log.e("yhj", "地址" + fileUrl);
                        try {
                            addIncrease(fileUrl);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }


                    } else {
                        Log.e("yhj", "错误" + e.getMessage());
                    }

                }

                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                    Log.e("yhj", "进度值" + value);
                }
            });


        } else {
            try {
                addIncrease(img);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    public void addIncrease(String img) throws ParseException {
        Book book = new Book();
        book.setTitle(bookEtTitle.getText().toString());
        book.setAuthor(bookEtAuthor.getText().toString());
        book.setIsbn(bookEtIsbn.getText().toString());
        book.setPublisher(bookEtPublisher.getText().toString());
        book.setPages(Integer.parseInt(bookEtPages.getText().toString()));
        book.setPrice(Double.parseDouble(bookEtPrice.getText().toString()));
        book.setPublication_date(new SimpleDateFormat("yyyy-MM").parse(bookEtPubdate.getText().toString()));
        book.setCategory(bookSpCategory.getSelectedItem().toString());
        book.setIntroduction(bookEtDescription.getText().toString());
        book.setBook_admin(HomeActivity.name);
        book.setBook_cover(img);

        book.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Toast.makeText(BookEntryActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(BookEntryActivity.this, "上传失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    public String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            bookIvCover.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "无法显示当前图片，请换一张！", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            default:
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        Uri uri = data.getData();
                        if (DocumentsContract.isDocumentUri(this, uri)) {
                            String documentId = DocumentsContract.getDocumentId(uri);
                            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                                String id = documentId.split(":")[1];//解析出数字格式的id
                                String selection = MediaStore.Images.Media._ID + "=" + id;
                                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                                imagePath = getImagePath(contentUri, null);
                            }
                        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                            imagePath = getImagePath(uri, null);
                        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                            imagePath = uri.getPath();
                        }
                        displayImage(imagePath);
                    } else {
                        Uri uri = data.getData();
                        imagePath = getImagePath(uri, null);
                        displayImage(imagePath);
                    }
                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(BookEntryActivity.this, "请授予该权限,负责会影响使用部分功能！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

}
