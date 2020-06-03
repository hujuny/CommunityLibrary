package com.yhj.communitylibrary.library.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yhj.communitylibrary.HomeActivity;
import com.yhj.communitylibrary.R;
import com.yhj.communitylibrary.library.bean.Borrow;
import com.yhj.communitylibrary.manage.Book;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class BookContentActivity extends AppCompatActivity {

    @BindView(R.id.library_borrow)
    ImageView libraryBorrow;
    @BindView(R.id.book_tv_title)
    TextView bookTvTitle;
    @BindView(R.id.book_tv_author)
    TextView bookTvAuthor;
    @BindView(R.id.book_tv_isbn)
    TextView bookTvIsbn;
    @BindView(R.id.book_tv_publisher)
    TextView bookTvPublisher;
    @BindView(R.id.book_tv_pages)
    TextView bookTvPages;
    @BindView(R.id.book_tv_pubdate)
    TextView bookTvPubdate;
    @BindView(R.id.book_tv_price)
    TextView bookTvPrice;
    @BindView(R.id.book_content_category)
    TextView bookContentCategory;
    @BindView(R.id.book_tv_admin)
    TextView bookTvAdmin;
    @BindView(R.id.book_tv_description)
    TextView bookTvDescription;
    @BindView(R.id.book_content_cover)
    ImageView bookContentCover;
    private String bookIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_content);
        ButterKnife.bind(this);


        Intent intent = getIntent();
        String book_admin = intent.getStringExtra("book_admin");
        String title = intent.getStringExtra("title");

        BookData(book_admin, title);
    }

    private void BookData(String book_admin, String title) {
        BmobQuery<Book> eq1 = new BmobQuery<Book>();
        eq1.addWhereEqualTo("book_admin", book_admin);
        BmobQuery<Book> eq2 = new BmobQuery<Book>();
        eq2.addWhereEqualTo("title", title);
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
                    Log.e("yhj", "长度" + object.size());
                }
            }
        });
    }

    private void initData(List<Book> object) {
        bookTvTitle.setText(object.get(0).getTitle());
        bookTvAuthor.setText(object.get(0).getAuthor());
        bookTvIsbn.setText(object.get(0).getIsbn());
        bookTvPublisher.setText(object.get(0).getPublisher());
        bookTvPages.setText(String.valueOf(object.get(0).getPages()));
        bookTvPubdate.setText(new SimpleDateFormat("yyyy-MM").format(object.get(0).getPublication_date()));
        bookTvPrice.setText(String.valueOf(object.get(0).getPrice()));
        bookContentCategory.setText(object.get(0).getCategory());
        bookTvAdmin.setText(object.get(0).getBook_admin());
        bookTvDescription.setText(object.get(0).getIntroduction());
        if (object.get(0).getBook_cover().contains("https")){
            object.get(0).setBook_cover(object.get(0).getBook_cover().replace("https","http"));
        }
        RequestOptions requestOptions = new RequestOptions();
        Glide.with(BookContentActivity.this).load(object.get(0).getBook_cover()).apply(requestOptions).into(bookContentCover);
        bookIcon = object.get(0).getBook_cover();
    }


    @OnClick(R.id.library_borrow)
    public void onClick() {
        if (bookTvAdmin.getText().toString().equals(HomeActivity.name)) {
            Toast.makeText(this, "您不能借自己的书！", Toast.LENGTH_SHORT).show();
        } else {
            BmobQuery<Borrow> eq1 = new BmobQuery<Borrow>();
            eq1.addWhereEqualTo("book_admin", bookTvAdmin.getText().toString());
            BmobQuery<Borrow> eq2 = new BmobQuery<Borrow>();
            eq2.addWhereEqualTo("title", bookTvTitle.getText().toString());
            List<BmobQuery<Borrow>> queries = new ArrayList<BmobQuery<Borrow>>();
            queries.add(eq1);
            queries.add(eq2);
            BmobQuery<Borrow> mainQuery = new BmobQuery<Borrow>();
            mainQuery.and(queries);
            mainQuery.findObjects(new FindListener<Borrow>() {
                @Override
                public void done(List<Borrow> object, BmobException e) {
                    if (e == null) {
                        if (object.size() > 0) {
                            if (object.get(object.size() - 1).getIs_restore() == 0) {
                                Toast.makeText(BookContentActivity.this, "图书已被" + object.get(object.size() - 1).getBorrow_name() + "借走！", Toast.LENGTH_SHORT).show();
                            } else {
                                showTimeSetDialog();
                            }
                        } else {
                            showTimeSetDialog();
                        }
                    }
                }
            });

        }

    }

    private void showTimeSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(this, R.layout.dialog_borrow_book, null);
        dialog.setView(view, 0, 0, 0, 0);
        CalendarView bookCalendar = view.findViewById(R.id.book_calendar);

        bookCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String time = year + "-" + (month + 1) + "-" + dayOfMonth;//选择归还时间
                if (time != null) {
                    Calendar calendar = Calendar.getInstance();
                    int cYear = calendar.get(Calendar.YEAR);
                    int cMonth = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    String time1 = cYear + "-" + cMonth + "-" + day;//当前时间

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    if (time1.compareTo(time) >= 0) {
                        Toast.makeText(BookContentActivity.this, "日期选择不合适！", Toast.LENGTH_SHORT).show();
                    } else {

                        Date date = null;
                        try {
                            date = formatter.parse(time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Borrow borrow = new Borrow();
                        borrow.setBook_admin(bookTvAdmin.getText().toString());
                        borrow.setBorrow_name(HomeActivity.name);
                        borrow.setTitle(bookTvTitle.getText().toString());
                        BmobDate bmobDate = new BmobDate(date);
                        borrow.setRestoreAt(bmobDate);
                        borrow.setIs_restore(0);
                        borrow.setIs_overdue(0);
                        borrow.setBook_icon(bookIcon);
                        borrow.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    Toast.makeText(BookContentActivity.this, "借阅成功", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        });
                    }

                }
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_FIRST_USER);
        super.onBackPressed();
    }
}
