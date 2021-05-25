package com.example.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.viewHolder> {

        ArrayList<ToDoModel> list ;
        Context context;



        public RecyclerAdapter(ArrayList<ToDoModel> list, Context context) {
            this.list = list;
            this.context = context;
        }
        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(context).inflate(R.layout.activity_task_view_sample, parent, false);
            return new viewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, int position) {
            final ToDoModel model = list.get(position);

            holder.name.setText(model.getName());
            holder.desc.setText(model.getDescription());
            holder.date.setText(model.getDate());


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class viewHolder extends RecyclerView.ViewHolder {

            TextView name, desc, date ;
            CheckBox box;

            public viewHolder(@NonNull View itemView) {
                super(itemView);

                name=itemView.findViewById(R.id.name);
                desc=itemView.findViewById(R.id.desc);
                date=itemView.findViewById(R.id.dt);
                box = itemView.findViewById(R.id.checkBox);
            }

        }

    }


