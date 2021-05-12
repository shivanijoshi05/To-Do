package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton newTask;
    RecyclerAdapter adapter;
    ArrayList<ToDoModel> taskList;
    FirebaseAuth fAuth;
    LinearLayout noTaskLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newTask = (FloatingActionButton) findViewById(R.id.newTask);
        noTaskLeft = findViewById(R.id.noTaskLeft);

        newTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, addTask.class));
            }
        });

        taskList = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.tasks);
        LinearLayoutManager l = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(l);
        adapter = new RecyclerAdapter(taskList, this);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(call);
        itemTouchHelper.attachToRecyclerView(recyclerView);

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
                startActivity(new Intent(MainActivity.this,Login.class));
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.getReference("todoList").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        taskList.clear();
                       // DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                        Log.w("ToDoapp", "getUser:onCancelled " + dataSnapshot.toString());
                        Log.w("ToDoapp", "count = " + String.valueOf(dataSnapshot.getChildrenCount()) + " values " + dataSnapshot.getKey());
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            ToDoModel todo = data.getValue(ToDoModel.class);
                            if(userId.contentEquals(todo.getUId())){
                                taskList.add(todo);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        if(taskList.isEmpty()){
                            noTaskLeft.setVisibility(View.VISIBLE);
                        }
                        else{
                            noTaskLeft.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("ToDoapp", "getUser:onCancelled", error.toException());
                    }
                });
    }

    ItemTouchHelper.SimpleCallback call = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getAdapterPosition();
            if(direction == ItemTouchHelper.LEFT){

               ToDoModel t = taskList.get(pos);
               DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

               ref.child("todoList").child(t.getId())
                 .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       taskList.remove(pos);
                       adapter.notifyItemRemoved(pos);
                       adapter.notifyDataSetChanged();
                       if(taskList.isEmpty()){
                           noTaskLeft.setVisibility(View.VISIBLE);
                       }
                       else{
                           noTaskLeft.setVisibility(View.INVISIBLE);
                       }

                       Toast.makeText(MainActivity.this,"Task Deleted.",Toast.LENGTH_SHORT).show();
                   }
               });
            }
        }
    };


 }

