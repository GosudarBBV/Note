package com.p.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.p.note.adapter.NoteAdapter;
import com.p.note.adapter.NoteItem;
import com.p.note.adapter.ShowToolbar;
import com.p.note.alert_dialog.MyDialogFragment;
import com.p.note.db.AppExecuter;
import com.p.note.db.MyDbManager;
import com.p.note.db.OnDataReceived;

import java.util.List;

public class Home extends AppCompatActivity implements ShowToolbar, OnDataReceived {

    private FloatingActionButton add_note;
    private MyDbManager myDbManager;
    private RecyclerView rcView;
    private TextView tvEmpty;
    private NoteAdapter noteAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.id_search);
        MenuItem delete = menu.findItem(R.id.menu_delete);
        MenuItem select = menu.findItem(R.id.menu_select_all);
        delete.setVisible(false);
        select.setVisible(false);
        SearchView sv = (SearchView) item.getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                readFromDb(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void init() {

        myDbManager = new MyDbManager(this);
        rcView = findViewById(R.id.rcView);
        tvEmpty = findViewById(R.id.tv_empty);
        noteAdapter = new NoteAdapter(this, this, tvEmpty, this);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        rcView.setAdapter(noteAdapter);
        getItemTouchHelper().attachToRecyclerView(rcView);

        add_note = findViewById(R.id.add_note);
        addNote();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
    }


    private void addNote() {
        add_note.setOnClickListener(view -> {
            Intent intent = new Intent(this, Edit.class);
            startActivity(intent);
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        myDbManager.openDb();
        readFromDb("");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDbManager.closeDb();

    }


    private ItemTouchHelper getItemTouchHelper() {
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                onSwipedDelete(viewHolder,direction);
            }
        });
    }


    @Override
    public void setVisibilityToolbar(boolean isEnable) {
        if (!isEnable) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    private void readFromDb(final String text){

        AppExecuter.getInstance().getSubIO().execute(new Runnable() {
            @Override
            public void run() {
                myDbManager.getFromDb(text, Home.this);
            }
        });

    }
    private void readFromDb(){

        AppExecuter.getInstance().getSubIO().execute(new Runnable() {
            @Override
            public void run() {
                myDbManager.getFromDb("", Home.this);
            }
        });

    }

    @Override
    public void onReceived(List<NoteItem> list) {
        AppExecuter.getInstance().getMainIO().execute(new Runnable() {
            @Override
            public void run() {
                noteAdapter.updateAdapter(list);
            }
        });
    }

    private void onSwipedDelete(RecyclerView.ViewHolder viewHolder, int direction){
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                noteAdapter.removeItem(viewHolder.getAdapterPosition(), myDbManager);
            }
        };

        DialogInterface.OnClickListener listenerUpdate = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                readFromDb();
            }
        };

        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.setDialogInterfacePB(listener);
        myDialogFragment.setDialogInterfaceNB(listenerUpdate);
        myDialogFragment.show(manager,"myDialog");
    }
}