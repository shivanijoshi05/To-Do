package com.example.todo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    FloatingActionButton addTask;
    EditText date, name, description;
    FirebaseAuth ref,fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        name = (EditText) findViewById(R.id.taskName);
        description = (EditText) findViewById(R.id.description);
        calendar = findViewById(R.id.calendar);
        date = (EditText) findViewById(R.id.dueDate);
        addTask = (FloatingActionButton) findViewById(R.id.addTask);
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
                Toast.makeText(addTask.this, "Task added successfully.", Toast.LENGTH_SHORT).show();
            }
        });

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            ToDoModel todo = (ToDoModel) extras.get("todo");
            if (todo != null) {

                name.setText(todo.getName());
                description.setText(todo.getDescription());
                date.setText(todo.getDate());

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
        switch (item.getItemId()){
            case R.id.signout:
                fAuth.signOut();
                startActivity(new Intent(addTask.this,Login.class));
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
            String d = dayOfMonth +"/" + month+1 +"/"+ year;
            date.setText(d);
    }

    void addingTasks(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = ref.getCurrentUser().getUid();
        String key = database.getReference("todoList").push().getKey();


        ToDoModel task = new ToDoModel();
        task.setUId(userId);
        task.setId(key);
        task.setName(name.getText().toString());
        task.setDescription(description.getText().toString());
        task.setDate(date.getText().toString());


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put( key, task.toFirebaseObject());
        database.getReference("todoList").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    finish();
                }
            }
        });
    }

}
