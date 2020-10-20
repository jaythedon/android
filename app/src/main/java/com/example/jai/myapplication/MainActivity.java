package com.example.jai.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private Button uploadButton;
    Service service;
    private TextView textView;
    private ImageView imageview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadButton = (Button) findViewById(R.id.mbutton);
        textView = (TextView) findViewById(R.id.mTextView);
        imageview = (ImageView)  findViewById(R.id.imageView);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        service  = new Retrofit.Builder().baseUrl("https://jaycartoon.herokuapp.com/").client(client).build().create(Service.class);




    }
    public void onButtonClick(android.view.View view){
        Toast.makeText(MainActivity.this,"on button",Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       // Toast.makeText(this,"inside on activty result",Toast.LENGTH_LONG).show();
        super.onActivityResult(requestCode, resultCode, data);
        String filePath = "";
        try {
            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                   // Toast.makeText(this, "inside result code", Toast.LENGTH_LONG).show();
                    Uri selectedImage = data.getData();
                   // Cursor returnCursor = getContentResolver().query(selectedImage,null,null,null
                   // ,null);
                  //  int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                  //  returnCursor.moveToFirst();
                   // Toast.makeText(this,"uri is"+selectedImage.toString(),Toast.LENGTH_LONG).show();
                   // String filename = getPath(selectedImage);
                   //
                    InputStream in = getContentResolver().openInputStream(selectedImage);
                     filePath = this.getFilesDir().getPath().toString() + "/testimg.jpg";
                    File file = new File(filePath);
                    OutputStream out = new FileOutputStream(file);
                    byte [] buf = new byte[1024];
                    int len;
                    while ((len= in.read(buf)) > 0){
                        out.write(buf,0,len);
                    }
                    out.close();
                    in.close();

                   // String filename = returnCursor.getString(nameIndex);
                  //  Toast.makeText(MainActivity.this, filename, Toast.LENGTH_LONG).show();
                   // File file = new File(filename);
                    Toast.makeText(this, "file name is" + filePath, Toast.LENGTH_SHORT).show();
                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);

                    retrofit2.Call<okhttp3.ResponseBody> req = service.postImage(body);
                    req.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            textView.setText("response");
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                            imageview.setVisibility(View.VISIBLE);
                            Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                            imageview.setImageBitmap(bmp);
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });

                }
            }
        }catch (Exception e){
            Log.e("class" ,e.toString());
            Toast.makeText(this,filePath, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public String getPath(Uri uri){
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri,filePathColumn,null,null,null);

      // int cursorIndex =  cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
       cursor.moveToFirst();
       int columnIndex  = cursor.getColumnIndex(filePathColumn[0]);

       String imgpath = cursor.getString(columnIndex);
       cursor.close();
       return imgpath;
    }
}
