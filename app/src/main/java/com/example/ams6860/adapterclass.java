package com.example.ams6860;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class adapterclass extends RecyclerView.Adapter<adapterclass.ViewHolder> {
    List<String> listid;
    List<String> listname;
    List<String> listattendnce;
alluserview objalluser;
    Context context;

    public adapterclass(Context c, List<String> listname, List<String> listid, List<String> attendance) {
        this.listid = listid;
        this.listname = listname;
        this.listattendnce = attendance;
        this.context = c;
    }

    @NonNull
    @Override
    public adapterclass.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.singleuser, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterclass.ViewHolder holder, int position) {
        holder.tvid.setText(listid.get(position));
        holder.tvname.setText(listname.get(position));
        holder.tvstatus.setText(listattendnce.get(position));
        holder.layout.setOnClickListener(view -> {
            Intent intent=new Intent(context,action_by_admin.class);
            intent.putExtra("id",listid.get(position));
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return listid.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvid, tvname, tvstatus;
        ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvid = itemView.findViewById(R.id.tvsingleid);
            tvname = itemView.findViewById(R.id.tvsinglename);
            tvstatus = itemView.findViewById(R.id.tvstatus);
            layout=itemView.findViewById(R.id.singlelayout);
        }
    }
}
