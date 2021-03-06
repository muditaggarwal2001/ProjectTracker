package com.example.mudit.projecttracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class NewProject extends AppCompatActivity {
    private TextView dateview;
    private EditText Ctitle, CNumber, IName, Pnumber, ProjectDesc;
    private RadioGroup status;

    private int editint=-1;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);
        if(Utils.manager==null)
            Utils.manager = new awsManager(getApplicationContext());
        Ctitle = (EditText) findViewById(R.id.Ctitle);
        CNumber = (EditText) findViewById(R.id.CNumber);
        IName = (EditText) findViewById(R.id.Iname);
        Pnumber = (EditText) findViewById(R.id.Pnumber);
        ProjectDesc = (EditText) findViewById(R.id.project_desc);
        status = (RadioGroup) findViewById(R.id.status);
        dateview = (TextView) findViewById(R.id.dateview);
        Button dateButton = (Button) findViewById(R.id.dateButton);
        if(getIntent().hasExtra(projectDetailFragment.ARG_ITEM_ID))
        {
            editint = Integer.parseInt(getIntent().getStringExtra(projectDetailFragment.ARG_ITEM_ID));
            fileContentmanager contentmanager = new fileContentmanager(Utils.ITEMS.get(editint));
            Ctitle.setText(contentmanager.getCtitle());
            CNumber.setText(contentmanager.getCNumber());
            IName.setText(contentmanager.getIName());
            Pnumber.setText(contentmanager.getPnumber());
            ProjectDesc.setText(contentmanager.getProjectDesc());
            dateview.setText(contentmanager.getDateview());
            if(contentmanager.getStatus().equalsIgnoreCase("Complete"))
                status.check(R.id.complete);
            else
                status.check(R.id.incomplete);
        }

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar currentdate = Calendar.getInstance();
                final int day = currentdate.get(Calendar.DAY_OF_MONTH);
                final int month = currentdate.get(Calendar.MONTH);
                final int year = currentdate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewProject.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedyear, int selectedmonth, int selectedday) {
                        dateview.setText(selectedday+"/"+(selectedmonth+1)+"/"+selectedyear);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
        uploadproject();
    }

    private String fetchProjectdata()
    {
        String result="";
        result+=Ctitle.getText()+";"+CNumber.getText()+";"+IName.getText()+";"+Pnumber.getText()+";"+ProjectDesc.getText()+";"+dateview.getText()+";";
        RadioButton rb=(RadioButton)findViewById(status.getCheckedRadioButtonId());
        result+=rb.getTag();
        return result;
    }

    private void uploadfile(File file)
    {
        TransferUtility transferUtility = Utils.manager.getTransferUtility();
        TransferObserver transferObserver = transferUtility.upload(Utils.bucket,file.getName(),file);
        transferObserverListener(transferObserver);
    }



    public void transferObserverListener(TransferObserver transferObserver) {
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                Toast.makeText(getApplicationContext(), "State Change" + state,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.d("Progress","Progress is "+((bytesCurrent/bytesTotal)*100)+"%");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.d("error","Error while transferring");
            }
        });
    }

    private void uploadproject() {
        Button submit = (Button)findViewById(R.id.sbutton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = fetchProjectdata();
                FileOutputStream outputStream;
                if(editint==-1) {
                    try {
                        Long x = System.currentTimeMillis() / 1000;
                        outputStream = openFileOutput(x.toString() + ".txt", MODE_PRIVATE);   //creats file in directory context.getFilesDir()
                        outputStream.write(data.getBytes("UTF-8")); //revert back using Arrays.tostring(bytes)
                        outputStream.close();
                        file = new File(getApplicationContext().getFilesDir().getPath()+"/"+x.toString() + ".txt");
                        uploadfile(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    file=Utils.ITEMS.get(editint);
                    try {
                        outputStream =  new FileOutputStream(file,false);
                        outputStream.write(data.getBytes("UTF-8")); //revert back using Arrays.tostring(bytes)
                        outputStream.close();
                        Utils.manager.deleteFile(file);   //deleting from aws s3, file passed for intention of providing it's key with which it was saved
                        uploadfile(file);   //uploading modified file with same name
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                finish();
            }
        });
    }


}
