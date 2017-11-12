package com.example.n5050.imageviewerrxjava;


import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

//import android.content.CursorLoader;

public class MainActivity extends AppCompatActivity {

    List<String> urls;
    private RecyclerView imageList;
    private ImageViewAdapter imageViewAdapter;
    public static final int PERMISSION_REQUEST=1234;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button=(Button) findViewById(R.id._fetch_contacts);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchContacts();
            }
        });
        imageList=(RecyclerView) findViewById(R.id.rv_images);


        fetchImages();

    }

    public void fetchImages(){
        Retrofit retrofit= new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://www.androidbegin.com/")
                .build();

        ImagesUriApi imagesUriApi=retrofit.create(ImagesUriApi.class);
        Observable<Country> observable= imagesUriApi.fetchCountryData();

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Country>() {
                    @Override
                    public void onSubscribe(Disposable d) {


                    }

                    @Override
                    public void onNext(Country country) {

                        urls=new ArrayList<>();
                        List<WorldPopulation> worldPopulations=country.getWorldpopulation();
                        for(int i=0;i<worldPopulations.size();i++){
                            urls.add(worldPopulations.get(i).getFlag());

                        }
                        imageViewAdapter=new ImageViewAdapter(getApplicationContext(),urls);
                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
                        imageList.setLayoutManager(linearLayoutManager);
                        imageList.setAdapter(imageViewAdapter);

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                    }

                    @Override
                    public void onComplete() {


                    }
                });

    }
    public void fetchContacts(){
        Toast.makeText(this,"Fetching Data..",Toast.LENGTH_LONG).show();
        checkPermission();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){
        if(requestCode==PERMISSION_REQUEST){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getSupportLoaderManager().initLoader(1,new Bundle(),contactsCursor).forceLoad();
            }
            else {
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},PERMISSION_REQUEST);
        }
        else{
            getSupportLoaderManager().initLoader(1,new Bundle(),contactsCursor).forceLoad();
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> contactsCursor=
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {

                    String[] projectionFields = new String[] { ContactsContract.Contacts._ID,
                            ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER};
                    CursorLoader cursorLoader=new CursorLoader(MainActivity.this,ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            projectionFields,null,null,null);
                    return cursorLoader;
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


                    extractToCSV(data);

                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {


                }
            };

    public void extractToCSV(Cursor c){
        File file=new File(this.getExternalFilesDir(null),"contacts.csv");
        try{

            file.createNewFile();
            CSVWriter csvWriter=new CSVWriter(new FileWriter(file),',');
            csvWriter.writeNext(c.getColumnNames());
            while(c.moveToNext()){
                String[] str={c.getString(0),c.getString(1),c.getString(2)};
                csvWriter.writeNext(str);
            }
            csvWriter.close();
            c.close();
            zip(file);


        }
        catch (Exception e){

            Log.e("Error",e.getMessage());

        }
    }

    public  void zip(File file){
        try{

            ZipFile zipFile=new ZipFile(this.getExternalFilesDir(null)+"/contacts.zip");
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            zipFile.addFile(file,parameters);
            Toast.makeText(this,"CSV extracted and Zipped",Toast.LENGTH_LONG).show();

        }
        catch (ZipException e){
            e.printStackTrace();
            Log.e("ZipException",e.getMessage());

        }

    }
}
