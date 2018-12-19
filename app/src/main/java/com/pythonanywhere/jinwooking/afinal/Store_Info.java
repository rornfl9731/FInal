package com.pythonanywhere.jinwooking.afinal;

//가게 정보 & 한줄리뷰작성

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import android.support.design.widget.NavigationView;
public class Store_Info extends AppCompatActivity {

    private Button sendbt;
    private EditText edit;
    private ImageView img;
    private TextView tx;
    private RatingBar snsR;
    private RatingBar thisR;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseDatabase md;
    private DatabaseReference mr;
    private ChildEventListener mChild;
    String dd;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    List<Object> Array = new ArrayList<Object>();
    FirebaseUser user;
    String uid;
    String Total;
    String Num;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_info_activity);
        Intent intent = getIntent();
        String data = intent.getStringExtra("value");

        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            }
        }else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때
            getWindow().setStatusBarColor(Color.BLACK);
        }

        ActionBar actionBar = getSupportActionBar();
        //Actionbar title
        actionBar.setTitle(data);
        //set back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        dd = data;
        data = data.replace("\n","");
        dd = dd.replace("\n","");
        user = FirebaseAuth.getInstance().getCurrentUser();
        //    uid = user.getUid();




        tx = (TextView) findViewById(R.id.textView);
        img = (ImageView) findViewById(R.id.img_view);
        sendbt = (Button) findViewById(R.id.button);
        edit = (EditText) findViewById(R.id.editText);
        listView = (ListView) findViewById(R.id.msg_list);
        snsR = findViewById(R.id.ratingBar);
        thisR =findViewById(R.id.ratingBar2);

        initDatabase();

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,new ArrayList<String>());
        listView.setAdapter(adapter);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });





//sns데이터 가져오기
        mReference = mDatabase.getReference(data);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();


                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    String msg2 = data.getValue().toString();
                    Array.add(msg2);
                    adapter.add(msg2);
                }

                adapter.notifyDataSetChanged();
                //listView.setSelection(adapter.getCount() -1);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //평점
        FirebaseDatabase.getInstance().getReference(data+"SNS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String stat = dataSnapshot.getValue().toString();
                String total = stat.split(" ")[0];
                Log.d("total",total);
                String num = stat.split(" ")[1];
                int To = Integer.parseInt(total);
                int Nu = Integer.parseInt(num);
                float star = (float)To/Nu;



                snsR.setNumStars(5);
                snsR.setRating(star);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference(data+"평점").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String stat = dataSnapshot.getValue().toString();
                String total = stat.split(" ")[0];
                Log.d("total",total);
                String num = stat.split(" ")[1];
                Log.d("num",num);
                Total = total;
                Num = num;

                int To = Integer.parseInt(total);
                int Nu = Integer.parseInt(num);
                float star = (float)To/Nu;
                Log.d("asdasd",String.valueOf(star));

                thisR.setNumStars(5);
                thisR.setRating(star);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//가게 이미지 삽입
        FirebaseDatabase.getInstance().getReference(data+"img").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String msg = dataSnapshot.getValue().toString();
                System.out.println(msg);
                Log.d("sex",msg);
                Picasso.get().load(msg).into(img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference(data+"inf").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String msg = dataSnapshot.getValue().toString();
                msg = msg.replace("B","\n");
                tx.setText(msg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        mr = md.getReference(data+"img");
//        mr.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String msg = dataSnapshot.getValue().toString();
//                System.out.println(msg);
////                Picasso.get()
////                        .load(msg)
////                        .into(img);
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        final Context context = this;
        // 한줄 리뷰 작성 & 사용자 계정으로도 리뷰 저장
        sendbt.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                SimpleDateFormat dayTime = new SimpleDateFormat("yyyy:MM:dd-hh:mm:ss");
                String str = dayTime.format(new Date(time));


                AlertDialog.Builder alret = new AlertDialog.Builder(context);
                alret.setTitle("평점 입력");
                alret.setMessage("5점 만점");
                String copy;





                final EditText et = new EditText(context);
                alret.setView(et);
                alret.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String value = et.getText().toString();
                        int a = Integer.parseInt(value) + Integer.parseInt(Total);

                        FirebaseDatabase md;
                        DatabaseReference mr;
                        mr = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference cd = mr.child(dd+"평점");
                        int n = Integer.parseInt(Num)+1;
                        cd.setValue(String.valueOf(a)+" "+String.valueOf(n));






                        Log.d("gi",value);
                        dialogInterface.dismiss();

                    }
                });

                alret.show();

                mReference = mDatabase.getReference();
                mReference.child(dd).push().setValue(edit.getText().toString());


                // mReference.child("사용자 리뷰").child(uid).push().setValue(edit.getText().toString());

                //mReference.child("사용자 리뷰").child(uid).push().setValue(edit.getText().toString());
                edit.setText("");
                edit.clearFocus();










            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initDatabase() {

        mDatabase = FirebaseDatabase.getInstance();

        mReference = mDatabase.getReference("log");
        mReference.child("log").setValue("check");

        mChild = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mReference.addChildEventListener(mChild);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReference.removeEventListener(mChild);
    }


}
