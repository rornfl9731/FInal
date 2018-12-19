package com.pythonanywhere.jinwooking.afinal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

//import android.support.design.widget.NavigationView;
//import com.google.android.gms.auth.api.Auth;
//import com.google.android.gms.auth.api.signin.GoogleSignInResult;
//import com.googlecode.tesseract.android.TessBaseAPI;


public class StartActivity extends AppCompatActivity  {
    private static final int CAMERA_REQUEST = 1888;
    private Button mBtnCameraView;
    private EditText mEditOcrResult;
    private String datapath = "";
    private String lang = "";
    private Button choo_bn;
    private static final int RC_SIGN_IN = 10;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private int ACTIVITY_REQUEST_CODE = 1;
    //static TessBaseAPI sTess;
    private TextView user_txt;
    private Button sech_btn;
    FirebaseUser user;
    Thread worker;

    String uid;
    private ImageView kak;
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    private Uri photoUri;
    private Bitmap real;
    private Socket socket;  //소켓생성
    BufferedReader in;      //서버로부터 온 데이터를 읽는다.
    PrintWriter out;
    String store_num;
    private TextView store;
    String corecore;
    private Button co_bu;
    private Button send_bt;
    String [] filename={"24시THE진국","24시신촌설렁탕","678CHICKEN (2)","678CHICKEN","7COFFEE (2)","7COFFEE","ABBETROAD","ABBR","ALCHON","AMIGO","AVENDUTCH","BEERKING","BETTERTHAN","BOBBYBOX","BURGER1987","B플랫","CAFEHO","CASAOLIVE","COFFEYONLY","COPPIA","CREAMY","CURRYYA","DOSMAS","EDIYACOFFEE","G2ZONE","GAMEJOY (2)","GAMEJOY","GAPA","GGGO","GONGCHA","HANDSUP","HANSDELI","HATAGAYA","HEADLINER","HODABANG","JUICY","LEOPUB","MEALPLANB","MOMSTOUCH (2)","MOMSTOUCH","OLIVECAFE","ONESHOT","OUTDAK (2)","OUTDAK","PACHI","PASTANPUB","PASTAPUB","RHOMBUS","SOBLINGCOFFEE","THAISPOON","THECOMICROOM","THEEDEN","THESTORY","THE치킨","TOGO (2)","TOGO","TOMATO","WAFFLEBANG","WANNACHU (2)","WANNACHU","WAYSIDECHICKEN","WHICAFFE","가메이","가시버시","가시버시밀면","경동부추삼겹살","경동부추삽겹살","고기앞으로","고로고로","고수찜닭","골목스테이크","국제미락","국제식당","궁중보쌈","그린비킹갈비","금산양꼬치 (2)","금산양꼬치 (3)","금산양꼬치","기가스호프","김씨솜씨깁밥","꼬치집","꼼보포차","끝판왕포차","남도뽀글이","낭풍","너울마당","노랑통닭","니뽕내뽕","다락","다우리식당","단톡방","닭살부부","닭큐멘터리 (2)","닭큐멘터리","당가네대박삽겹","덴세츠스시","도토리 (2)","도토리","돈까스","돈까스전문점","돈마니","동경야시장","동궁찜닭","동네아저씨돈찌","동네아저씨치킨돈찌","디델리라볶기 (2)","디델리라볶기","디에덴","딱좋아포차 (2)","딱좋아포차","딴따라포차","떡볶이에반한닭","뚱아뚱아오릿대","랄라떡볶이","레커훈스","마루 (2)","마루 (3)","마루","마시는놀이터 (2)","마시는놀이터 (3)","마시는놀이터 (4)","마시는놀이터","맛사랑","매운애","맥주BARKET","맥주집","면사랑 (2)","면사랑","명동손만두 (2)","명동손만두","명동찌개마을","명량핫도그","무대포회해물구이","무한도담","뭉크","미로곱창","미로뮤지엄","발해","밥FULL","백채김치찌개","버거크루","베스킨라빈스","베트남쌀국수 (2)","베트남쌀국수","별마루","병헤는밤 (2)","병헤는밤","보드카페","봉구스밥버거","북경깐풍기","불고기클라쓰","비담비 (2)","비담비","비빔밥상","빠삭치킨","빽다방","뽀댕이","뽑기매니아","산쪼메","삼겹살에소주한잔","삼겹싸롱","삼구포차 (2)","삼구포차 (3)","삼구포차 (4)","삼구포차","삼시세끼","삼육포차","삽겹살에소주한잔","샤브샤브 (2)","샤브샤브","세츠라멘","세프카레전문점","소문난이웃집떡볶이","소반 (2)","소반","수꼬치","수라국수 (2)","수라국수 (3)","수라국수","수철이네왕새우튀김","술다방","술동아리","숯불구이먹방","시골집","시내비골","신불떡볶이","신전떡볶이","심양꼬치","싸움의고수","아리랑컵밥","아빠왕족발","아싸요우","아이원PC","안녕하세요오하요 (2)","안녕하세요오하요","안암아줌마치킨 (2)","안암아줌마치킨","앗싸닭발","애비로드","야우리밥먹자","연길양꼬치 (2)","연길양꼬치","영종식당","영지숯불갈비","오겡끼데쓰까","오대만족","오뎅","오모가리삼겹살","오사카부루스","오오우","옹기종기회","완벽김밥","왕돈까스","우마이","우선소곱창 (2)","우선소곱창","우향","우향곰탕","원조가격파괴 (2)","원조가격파괴","원조통계란영양빵","의정부부대찌개","이구포차 (2)","이구포차","이모네빈대떡 (2)","이모네빈대떡","이모떡즉석떡볶이전문점","이치라멘돈가츠 (2)","이치라멘돈가츠","인하빈대떡","인하칼국수 (2)","인하칼국수","일비닭갈비파전","적성루","전주식당콩나물해장국통삽겹","정일품","종로닭한마리","종로빈대떡","종로순대","종록닭한마리","죠아저씨치킨떡볶이","주정뱅이수다","준호네부대찌개","즉석떡볶이이모떡 (2)","즉석떡볶이이모떡","진희분식","짜장전설","짝노패밀리","짝태와노가리 (2)","짝태와노가리 (3)","짝태와노가리","찌배사랑","챕터원스테이크PUB","천지인 (2)","천지인","첫잔","청년다방","청진동해장국","청춘식당","체게바라","체게바라2","촌닭","최군맥주","춘천닭갈비 (2)","춘천닭갈비","춤추는갈매기 (2)","춤추는갈매기","취엔","치즈를사랑한찜닭","커피에반하다24","케밥집 (2)","케밥집 (3)","케밥집","콩불","콩뽁는다락방","킹콩순두부 (2)","킹콩순두부 (3)","킹콩순두부","탄포포","탕진잼","태화성","파불고기","팥지콩지","평산옥","포차끝판왕","퓨전포차","핀비어킹","필바든치킨","하꼬멘","하나마루","하이와플","하카타라멘","한솥","해머","허수아비돈까스","호타루 (2)","호타루","활어랑조개구이랑옹기종기","황기족발"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();


        setContentView(R.layout.activity_start);
        View view = getWindow().getDecorView();

        mEditOcrResult = (EditText) findViewById(R.id.edit_ocrresult);
        choo_bn = findViewById(R.id.choo_btn);
        mEditOcrResult.clearFocus();

        sech_btn = (Button)findViewById(R.id.btn_search);
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


        worker = new Thread() {    //worker 를 Thread 로 생성
            public void run() { //스레드 실행구문
                try {
//소켓을 생성하고 입출력 스트립을 소켓에 연결한다.
                    //socket = new Socket("35.187.202.115", 7070); //소켓생성

                    socket = new Socket("172.22.144.1", 7777); //소켓생성
                    //socket = new Socket("192.168.25.18", 7777);
                    //socket = new Socket("122.44.255.223",7777);
                    out = new PrintWriter(socket.getOutputStream(), true); //데이터를 전송시 stream 형태로 변환하여                                                                                                                       //전송한다.
                    in = new BufferedReader(new InputStreamReader(
                            socket.getInputStream())); //데이터 수신시 stream을 받아들인다.

                } catch (IOException e) {
                    e.printStackTrace();
                }

//소켓에서 데이터를 읽어서 화면에 표시한다.
                try {
                    while (true) {
                        store_num = in.readLine(); // in으로 받은 데이타를 String 형태로 읽어 data 에 저장
                        store.post(new Runnable() {
                            public void run() {
                               // store.setText(store_num); //글자출력칸에 서버가 보낸 메시지를 받는다.

                                //mEditOcrResult.setText(filename[Integer.parseInt(store_num)]);
                                mEditOcrResult.setText(store_num);
                                Log.d("resua",store_num);
                            }
                        });
                    }
                } catch (Exception e) {
                }
            }
        };

        worker.start();  //onResume()에서 실행.

//여기





        kak = findViewById(R.id.imageView3);


        store = findViewById(R.id.store);

        mBtnCameraView = (Button) findViewById(R.id.btn_camera);
        mBtnCameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//
//
//
//                out.print("hi");
                sendTakePhotoIntent();

                //worker.start();
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                Resources res = getResources();


            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        send_bt = findViewById(R.id.send_btn);
        send_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Resources res = getResources();




//                Bitmap bitmap = BitmapFactory.decodeResource(res,R.drawable.er);
//                bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
//                byte[] image = baos.toByteArray();
//                String asd = android.util.Base64.encodeToString(image, android.util.Base64.NO_WRAP);

                //Log.d("sex",asd);


                //String data = input.getText().toString(); //글자입력칸에 있는 글자를 String 형태로 받아서 data에 저장


                out.println(corecore); //data를   stream 형태로 변형하여 전송.  변환내용은 쓰레드에 담겨 있다.




//                Intent intent = new Intent(getApplicationContext(),Store_Info.class);
//                intent.putExtra("value",filename[1]);
//
//                startActivity(intent);




            }
        });

//        co_bu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = getIntent();
//                String data = intent.getStringExtra("sex");
//                Log.d("ssex",data);
//                //worker.start();
//               // Log.d("pppzzz",corecore);
//                //out.print(corecore);
//            }
//        });





        final Context context = this;

        sech_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditOcrResult.getText().toString().equals("")){
                    AlertDialog.Builder alret = new AlertDialog.Builder(context);
                    alret.setTitle("검색 실패");
                    alret.setMessage("없는 상점 입니다");
                    alret.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    alret.show();


                }else {
                    Intent intent = new Intent(getApplicationContext(),Store_Info.class);
                    intent.putExtra("value",mEditOcrResult.getText().toString());
                    mEditOcrResult.setText("");

                    startActivity(intent);
                }

            }
        });

        choo_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PostsListActivity.class);
                startActivity(intent);

            }
        });



        SignInButton btn = (SignInButton)findViewById(R.id.log_but);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//
//                startActivityForResult(signInIntent, RC_SIGN_IN);
//
//            }
//        });



    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;

            try {
                exif = new ExifInterface(imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegrees(exifOrientation);
            } else {
                exifDegree = 0;
            }


            ((ImageView)findViewById(R.id.imageView3)).setImageBitmap(rotate(bitmap, exifDegree));

            //store.setText(corecore);
            TextView qq = (TextView)findViewById(R.id.textView2);
            qq.setText(corecore);
            qq.setVisibility(View.GONE);
            //((TextView)findViewById(R.id.textView2)).setText(corecore);

        }
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        real = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Resources res = getResources();

        real.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] image = baos.toByteArray();


        corecore = android.util.Base64.encodeToString(image, android.util.Base64.NO_WRAP);


        //store.setText(corecore);
        Log.d("realsex",corecore);
//        out.print("hi");


        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    private void sendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);



                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }



}
