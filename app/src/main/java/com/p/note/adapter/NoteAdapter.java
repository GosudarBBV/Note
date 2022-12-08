package com.p.note.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.p.note.Edit;
import com.p.note.R;
import com.p.note.db.MyConstants;
import com.p.note.db.MyDbManager;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {

    private Context context;
    private List<NoteItem> noteItems;
    private TextView tvEmpty;

    //selection
    private Activity activity;
    MainViewModel mainViewModel;

    boolean isEnable = false;
    boolean isSelectAll = false;
    private MyDbManager myDbManager;
    private List<NoteItem> selectList = new ArrayList<>();

    private ShowToolbar showToolbar;


    public NoteAdapter(Context context,TextView tvEmpty) {
        this.context = context;
        noteItems = new ArrayList<>();
        this.tvEmpty = tvEmpty;
    }
    public NoteAdapter(Context context,Activity activity,TextView tvEmpty,ShowToolbar showToolbar) {
        noteItems = new ArrayList<>();
        this.context = context;
        this.activity = activity;
        this.tvEmpty = tvEmpty;
        this.showToolbar = showToolbar;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent,false);

        mainViewModel = ViewModelProviders.of((FragmentActivity) activity)
                .get(MainViewModel.class);
        return new MyViewHolder(view,context,noteItems);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.setData(noteItems.get(position).getTitle());
        myDbManager = new MyDbManager(context);



        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isEnable){

                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            MenuInflater menuInflater = mode.getMenuInflater();
                            menuInflater.inflate(R.menu.menu,menu);
                            MenuItem item = menu.findItem(R.id.id_search);
                            item.setVisible(false);

                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                            isEnable = true;
                            ClickItem(holder);

                            mainViewModel.getText().observe((LifecycleOwner) activity, new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    mode.setTitle(String.format("%s selected",s));
                                }
                            });
                            showToolbar.setVisibilityToolbar(isEnable);
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            int id = item.getItemId();

                            switch (id){
                                case R.id.menu_delete:
                                    for (NoteItem s : selectList){

                                        noteItems.remove(s);
                                        deleteFromDB(holder.getAdapterPosition());
                                    }

                                    if (noteItems.size() == 0){

                                        tvEmpty.setVisibility(View.VISIBLE);
                                    }

                                    mode.finish();
                                    break;
                                case R.id.menu_select_all:

                                    if (selectList.size() == noteItems.size()){

                                        isSelectAll = false;
                                        selectList.clear();

                                    } else {

                                        isSelectAll = true;

                                        selectList.clear();

                                        selectList.addAll(noteItems);
                                    }

                                    mainViewModel.setText(String.valueOf(selectList.size()));

                                    notifyDataSetChanged();
                                    break;
                            }

                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {

                            isEnable = false;
                            isSelectAll = false;
                            selectList.clear();
                            notifyDataSetChanged();
                            showToolbar.setVisibilityToolbar(isEnable);
                        }
                    };

                    ((AppCompatActivity) view.getContext()).startActionMode(callback);
                }else {

                    ClickItem(holder);
                }

                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isEnable){
                    ClickItem(holder);
                }else {
                    Intent intent = new Intent(context, Edit.class);
                    intent.putExtra(MyConstants.LIST_ITEM_INTENT,noteItems.get(holder.getAdapterPosition()));
                    intent.putExtra(MyConstants.EDIT_STATE,false);
                    context.startActivity(intent);
                    Toast.makeText(activity, "You Clicked " + noteItems.get(holder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (isSelectAll){
            holder.ivCheckBox.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.LTGRAY);

        }else {
            holder.ivCheckBox.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

    }


    @Override
    public int getItemCount() {
        return noteItems.size();
    }

    private void ClickItem(MyViewHolder holder) {

        NoteItem s = noteItems.get(holder.getAdapterPosition());

        if (holder.ivCheckBox.getVisibility() == View.GONE){
            holder.ivCheckBox.setVisibility(View.VISIBLE);

            holder.itemView.setBackgroundColor(Color.LTGRAY);

            selectList.add(s);
        } else {

            holder.ivCheckBox.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            selectList.remove(s);
        }

        mainViewModel.setText(String.valueOf(selectList.size()));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private Context context;
        private List<NoteItem> noteItems;


        private ImageView ivCheckBox;

        public MyViewHolder(@NonNull View itemView,Context context,List<NoteItem> noteItems){
            super(itemView);
            this.context = context;
            this.noteItems = noteItems;
            tvTitle = itemView.findViewById(R.id.note_item);
            //itemView.setOnClickListener(this);

            ivCheckBox =itemView.findViewById(R.id.iv_check_box);

        }
        public void setData(String title){
            tvTitle.setText(title);
        }

//        @Override
//        public void onClick(View view) {
//            Intent intent = new Intent(context, Edit.class);
//            intent.putExtra(MyConstants.LIST_ITEM_INTENT,noteItems.get(getAdapterPosition()));
//            intent.putExtra(MyConstants.EDIT_STATE,false);
//            context.startActivity(intent);
//        }
    }

    public void updateAdapter(List<NoteItem> newList){
        noteItems.clear();
        noteItems.addAll(newList);
        notifyDataSetChanged();
    }

    public void removeItem(int pos, MyDbManager dbManager){
        dbManager.delete(noteItems.get(pos).getId());
        noteItems.remove(pos);
        notifyItemRangeChanged(0, noteItems.size());
        notifyItemRemoved(pos);

    }

    public void deleteFromDB(int pos){
        myDbManager.openDb();
        myDbManager.getFromDb();
        myDbManager.delete(noteItems.get(pos).getId());
        notifyItemRangeChanged(0, noteItems.size());
        notifyItemRemoved(pos);
        myDbManager.closeDb();
    }

}
