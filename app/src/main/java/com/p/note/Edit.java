package com.p.note;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.p.note.adapter.NoteAdapter;
import com.p.note.adapter.NoteItem;
import com.p.note.db.AppExecuter;
import com.p.note.db.MyConstants;
import com.p.note.db.MyDbManager;

public class Edit extends AppCompatActivity implements View.OnClickListener {

    private final int PICK_IMAGE_CODE = 123;

    ImageButton imAddImage,imEditImage,imDeleteImage;
    ImageView imNewImage;
    EditText edTitle,edDesc;

    private TextView btn_save;
    ConstraintLayout imageLayout;

    private String tempUri = "empty";
    private boolean isEditState = true;
    private NoteItem item;


    private MyDbManager myDbManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
        getMyIntents();

    }

    @Override
    protected void onResume() {
        super.onResume();
        myDbManager.openDb();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDbManager.closeDb();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_CODE && data != null){
            tempUri = data.getData().toString();
            imNewImage.setImageURI(data.getData());
            getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION);



        }
    }

    private void init(){
        myDbManager = new MyDbManager(this);
        edDesc = findViewById(R.id.edDesc);
        edTitle = findViewById(R.id.edTitle);
        btn_save = findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSave();
            }
        });

        imNewImage = findViewById(R.id.imNewImage);

        imAddImage = findViewById(R.id.imAddImage);
        imEditImage = findViewById(R.id.imEditImage);
        imDeleteImage = findViewById(R.id.imDeleteImage);
        imageLayout = findViewById(R.id.imageContainer);


        imAddImage.setOnClickListener(this);
        imEditImage.setOnClickListener(this);
        imDeleteImage.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imAddImage:
                onShowAddImage();
                break;
            case R.id.imEditImage:
                onClickChooseImage();
                break;
            case R.id.imDeleteImage:
                onDeleteImage();
                break;
        }
    }

    private void onClickSave(){
        final String title = edTitle.getText().toString();
        final String desc = edDesc.getText().toString();
        if(title.equals("") || desc.equals("")){
            Toast.makeText(this, "all field must fill", Toast.LENGTH_SHORT).show();
        } else{
            if (isEditState){
                AppExecuter.getInstance().getSubIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        myDbManager.insertToDb(title,desc,tempUri);
                    }
                });
                Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();

            }else {

                myDbManager.updateItem(title,desc,tempUri,item.getId());
            }

            myDbManager.closeDb();
            finish();


        }
    }

    private void getMyIntents(){

        Intent i = getIntent();
        if (i != null){

            item = (NoteItem) i.getSerializableExtra(MyConstants.LIST_ITEM_INTENT);
            isEditState = i.getBooleanExtra(MyConstants.EDIT_STATE,true);

            if (!isEditState){
                edTitle.setText(item.getTitle());
                edDesc.setText(item.getDesc());
                if (!item.getUri().equals("empty")){
                    tempUri = item.getUri();
                    imageLayout.setVisibility(View.VISIBLE);
                    imNewImage.setImageURI(Uri.parse(item.getUri()));
                }
            }
        }
    }

    public void onShowAddImage(){
        imageLayout.setVisibility(View.VISIBLE);
    }

    public void onClickChooseImage() {
        Intent chooser =  new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooser.setType("image/*");
        startActivityForResult(chooser, PICK_IMAGE_CODE);

    }

    public void onDeleteImage(){
        imageLayout.setVisibility(View.GONE);
        imNewImage.setImageResource(R.drawable.ic_image_def);
    }

}