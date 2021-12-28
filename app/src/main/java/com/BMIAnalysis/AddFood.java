package com.BMIAnalysis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddFood extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText etNameFood, etClary;
    Spinner spinner;
    ImageView imgFood;
    Button btnUploadPhoto, btnSave;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    StorageReference references;
    Uri imgUri, uri;
    Foods listFoods;
    String uIds, categorty;
    ProgressDialog dialog;
    int selspinner = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        etNameFood = findViewById(R.id.et_nameFood);
        imgFood = findViewById(R.id.imgFood);
        btnSave = findViewById(R.id.btnSave);
        etClary = findViewById(R.id.etClary);
        spinner = findViewById(R.id.spinner);
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uIds = firebaseAuth.getUid();
        references = FirebaseStorage.getInstance().getReference();
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 20);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        categorty = spinner.getSelectedItem().toString();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etNameFood.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "يجب ادخال اسم الوجبة", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etClary.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "يحب ادخال عدد السعرات ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (uri == null) {
                    Toast.makeText(getApplicationContext(), "الرجاء اختيار صورة", Toast.LENGTH_LONG).show();
                    return;
                }
                listFoods = new Foods();
                listFoods.setuId(uIds);

                listFoods.setClaryFoods(etClary.getText().toString());
                listFoods.setNameFoods(etNameFood.getText().toString());
                listFoods.setCategoryFoodsName(selspinner + "");
                listFoods.setFBUri(uri + "");
                listFoods.setuId(uIds);

                firebaseFirestore.collection("food").add(listFoods)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    etClary.setText("");
                                    spinner.setSelection(0);
                                    etNameFood.setText("");
                                    Glide.with(getApplicationContext()).load(R.drawable.ic_launcher_background).into(imgFood);
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20) {
            if (resultCode == Activity.RESULT_OK) {
                imgUri = data.getData();
                Glide.with(getApplicationContext()).load(imgUri).into(imgFood);
                upload(imgUri);
            }
        }
    }

    private void upload(Uri image) {
        dialog = ProgressDialog.show(this, "انتظر ", "الرجاء الانتظار قليلا", true);
        final StorageReference reference = this.references.child(uIds);
        reference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        AddFood.this.uri = uri;
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        categorty = adapterView.getItemAtPosition(i).toString();
        selspinner = adapterView.getSelectedItemPosition();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static class splashActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i=new Intent(getBaseContext(), LogIn.class);
                    startActivity(i);
                }
            },5000);

        }
    }

    public class EditFood extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
        EditText etNameFood, etClary;
        TextView next;
        Spinner spinner;
        ImageView imgFood;
        Button btnUploadPhoto, btnSave;
        Uri uri;
        String firbaseUr,nameFood , claryFood, documentId,uId, categorys;;
        Intent intent;
        ProgressDialog dialog;
        int id;
        FirebaseFirestore firebaseFirestore;
        FirebaseAuth firebaseAuth;
        StorageReference storageReference;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.edit_food);
            etNameFood = findViewById(R.id.etNameFood);
            btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
            etClary = findViewById(R.id.etClary);
            spinner = findViewById(R.id.spinner);
            imgFood = findViewById(R.id.imgFood);
            btnSave = findViewById(R.id.btnSave);
            next=findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddFood.this,LogIn.class);
                    startActivity(intent);
                }
            });
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            uId = firebaseAuth.getUid();
            intent = getIntent();
            nameFood = intent.getStringExtra("name");
            claryFood = intent.getStringExtra("c");
            categorys = intent.getStringExtra("cat");
            documentId = intent.getStringExtra("dId");
            firbaseUr = intent.getStringExtra("image");
            id = intent.getIntExtra("id",1);

            etNameFood.setText(nameFood);
            etClary.setText(claryFood);
            etNameFood.setText(nameFood);
            Glide.with(getApplicationContext()).load(firbaseUr).into(imgFood);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.planets_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(id);

            spinner.setOnItemSelectedListener(this);
            storageReference = FirebaseStorage.getInstance().getReference();

            btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent opengalary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(opengalary, 20);
                }
            });
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etNameFood.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "الاسم اجباري ", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (etClary.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "السعرات اجبارية ", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Map<String, Object> stringObjectHashMap = new HashMap<>();
                    String claryFoods=etClary.getText().toString();
                    String nameFoods=etNameFood.getText().toString();


                    stringObjectHashMap.put("claryFoods", claryFoods);
                    stringObjectHashMap.put("fburi", firbaseUr);
                    stringObjectHashMap.put("nameFoods", nameFoods);
                    stringObjectHashMap.put("categoryFoodsName", id);
                    firebaseFirestore.collection("food").document(documentId)
                            .update(stringObjectHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                               startActivity(new Intent(getApplicationContext() , ListFood.class));

                        }
                    });

                }
            });
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 20) {
                if (resultCode == Activity.RESULT_OK) {
                    uri = data.getData();
                    uplode(uri);
                }
            }
        }

        private void uplode(Uri uri1) {
            dialog = ProgressDialog.show(this, "","استنا شوي ", true);
            final StorageReference reference = storageReference.child(uId);
            reference.putFile(uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplicationContext()).load(uri).into(imgFood);
                            firbaseUr = uri.toString();
                            dialog.dismiss();
                        }
                    });
                }
            });

        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            categorys = adapterView.getItemAtPosition(i).toString();
            id = adapterView.getSelectedItemPosition();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) { }
    }
}