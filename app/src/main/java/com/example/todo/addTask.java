package com.example.todo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class addTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ImageView calendar;
    FloatingActionButton addTask,update;
    EditText date, name, description;
    FirebaseAuth ref,fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        name =  findViewById(R.id.taskName);
        description =  findViewById(R.id.description);
        calendar = findViewById(R.id.calendar);
        date =  findViewById(R.id.dueDate);
        addTask =  findViewById(R.id.addTask);
        update=findViewById(R.id.update);
        ref = FirebaseAuth.getInstance();

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingTasks();
                Toast.makeText(addTask.this, "Task added", Toast.LENGTH_SHORT).show();
            }
        });
        if (getIntent().getExtras() != null) {
            String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Bundle extras = getIntent().getExtras();
            ToDoModel t = (ToDoModel)extras.get(user);
            if (t != null) {
                name.setText(t.getName());
                description.setText(t.getDescription());
                date.setText(t.getDate());
                
                addTask.setVisibility(View.INVISIBLE);
                update.setVisibility(View.VISIBLE);
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String taskName = name.getText().toString().trim();
                        String taskDescription = description.getText().toString().trim();
                        String dueDate = date.getText().toString().trim();

                        if(TextUtils.isEmpty(taskName)){
                            name.setError("Enter the task name ");
                            return;
                        }
                        if(TextUtils.isEmpty(taskDescription)){
                            description.setError("Enter the description ");
                            return;
                        }
                        if(TextUtils.isEmpty(dueDate)){
                            date.setError("Enter the due date ");
                            return;
                        }
                        updatingTasks(t);
                        Toast.makeText(addTask.this, "Task updated", Toast.LENGTH_SHORT).show();
                    }
                });

                
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        fAuth = FirebaseAuth.getInstance();
        if (item.getItemId() == R.id.signout) {
            fAuth.signOut();
            startActivity(new Intent(addTask.this, Login.class));
        }
        return super.onOptionsItemSelected(item);
    }
    private void showDatePickerDialog () {
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    this,
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();

    }
    @Override
    public void onDateSet (DatePicker view,int year, int month, int dayOfMonth){
        month+=1;   
        String d = dayOfMonth +"/" + month +"/"+ year;
        date.setText(d);
    }

    void addingTasks(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = ref.getCurrentUser().getUid();
        String key = database.getReference(userId).push().getKey();

        ToDoModel task = new ToDoModel();

        task.setId(key);
        task.setName(name.getText().toString());
        task.setDescription(description.getText().toString());
        task.setDate(date.getText().toString());


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put( key, task.toFirebaseObject());
        database.getReference(userId).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    finish();
                }
            }
        });
    }

    void updatingTasks(ToDoModel task){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = ref.getCurrentUser().getUid();

        task.setName(name.getText().toString());
        task.setDescription(description.getText().toString());
        task.setDate(date.getText().toString());

        database.getReference(userId).child(task.getId()).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
            }
        });


    }

}