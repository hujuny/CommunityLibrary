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
import android.view.Gravity;
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
import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.manage.Book;

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
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * author : yhj
 * date  :2020/4/29
 * desc   :我的图书，图书信息可修改
 */
public class MyBookContentActivity extends AppCompatActivity {

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

    @BindView(R.id.book_et_admin)
    EditText bookEtAdmin;
    @BindView(R.id.my_book_title)
    TextView myBookTitle;
    @BindView(R.id.book_bt_save)
    Button bookBtSave;
    private String imagePath;
    private String[] mStringsItem;
    private String book_admin;
    private String img;
    private String objectId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Immersive.setContentView(this, R.layout.activity_book_entry, R.color.myblue, R.color.black, false, false);

        ButterKnife.bind(this);


        Intent intent = getIntent();
        book_admin = intent.getStringExtra("book_admin");
        img = intent.getStringExtra("book_cover");

        myBookTitle.setText("图书详情");
        bookEtDescription.setGravity(Gravity.LEFT);


        bookSpCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                mStringsItem = getResources().getStringArray(R.array.sping_text);
                TextView tv = (TextView) arg1;
                tv.setTextColor(Color.GRAY);
                tv.setTextSize(14f);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        queryBookData(book_admin, img);


    }

    private void queryBookData(String book_admin, String img) {
        BmobQuery<Book> eq1 = new BmobQuery<Book>();
        eq1.addWhereEqualTo("book_admin", book_admin);
        BmobQuery<Book> eq2 = new BmobQuery<Book>();
        eq2.addWhereEqualTo("book_cover", img);
        List<BmobQuery<Book>> queries = new ArrayList<BmobQuery<Book>>();
        queries.add(eq1);
        queries.add(eq2);
        BmobQuery<Book> mainQuery = new BmobQuery<Book>();
        mainQuery.and(queries);
        mainQuery.findObjects(new FindListener<Book>() {
            @Override
            public void done(List<Book> object, BmobException e) {
                if (e == null) {
                    initData(object);
                }
            }
        });
    }

    private void initData(List<Book> object) {

        objectId = object.get(0).getObjectId();
        for (int i = 0; i < mStringsItem.length; i++) {
            if (mStringsItem[i].equals(object.get(0).getCategory())) {
                bookSpCategory.setSelection(i);
            }
        }
        bookEtTitle.setText(object.get(0).getTitle());
        bookEtAuthor.setText(object.get(0).getAuthor());
        bookEtIsbn.setText(object.get(0).getIsbn());
        bookEtPublisher.setText(object.get(0).getPublisher());
        bookEtPages.setText(String.valueOf(object.get(0).getPages()));
        bookEtPubdate.setText(new SimpleDateFormat("yyyy-MM").format(object.get(0).getPublication_date()));
        bookEtPrice.setText(String.valueOf(object.get(0).getPrice()));
        bookEtAdmin.setText(object.get(0).getBook_admin());
        bookEtDescription.setText(object.get(0).getIntroduction());
        if (object.get(0).getBook_cover().contains("https")){
            object.get(0).setBook_cover(object.get(0).getBook_cover().replace("https","http"));
        }
        RequestOptions requestOptions = new RequestOptions();
        Glide.with(MyBookContentActivity.this).load(object.get(0).getBook_cover()).apply(requestOptions).into(bookIvCover);
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

                    bookUpdate();

                }
                break;
        }
    }

    private void bookUpdate() {

        if (imagePath != null) {
            BmobFile bmobFile = new BmobFile(new File(imagePath));
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        String fileUrl = bmobFile.getFileUrl();
                        Log.e("yhj", ""+fileUrl);
                        try {
                            ChangeBook(fileUrl);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                            Toast.makeText(MyBookContentActivity.this, "信息格式异常，请检查！", Toast.LENGTH_SHORT).show();

                        }
                    }else {
                        Log.e("yhj", "错误上传"+e.getMessage());
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
                ChangeBook(img);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(this, "信息格式异常，请检查！", Toast.LENGTH_SHORT).show();
            }
        }


    }


    public void ChangeBook(String img) throws ParseException {
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
        book.setBook_admin(book_admin);
        book.setBook_cover(img);
        book.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(MyBookContentActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyBookContentActivity.this, "更新失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MyBookContentActivity.this, "请授予该权限,负责会影响使用部分功能！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}
