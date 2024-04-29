package com.example.filetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        // variable for our array list and context
        private ArrayList<DataModal> courseModalArrayList;
        private Context context;

        // constructor
        Adapter(ArrayList<DataModal> courseModalArrayList, Context context) {
            this.courseModalArrayList = courseModalArrayList;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // on below line we are inflating our layout
            // file for our recycler view items.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // on below line we are setting data
            // to our views of recycler view item.
            DataModal modal = courseModalArrayList.get(position);
            holder.NameTV.setText(modal.getEmployeeName());
            holder.DivisionTV.setText(modal.getDivisionName());

        }

        @Override
        public int getItemCount() {
            // returning the size of our array list
            return courseModalArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            // creating variables for our text views.
            private TextView NameTV, DivisionTV;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // initializing our text views
                NameTV = itemView.findViewById(R.id.idName);
                DivisionTV = itemView.findViewById(R.id.idDivision);
            }
        }
    }


