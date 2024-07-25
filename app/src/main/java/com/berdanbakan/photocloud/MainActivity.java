package com.berdanbakan.photocloud;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.berdanbakan.photocloud.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    ArrayList<Memory> memoryArrayList;
    MemoryAdapter memoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets.consumeSystemWindowInsets();


        });
        memoryArrayList=new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        memoryAdapter=new MemoryAdapter(memoryArrayList);
        binding.recyclerView.setAdapter(memoryAdapter);


        getData();
    }
        private void getData(){
        try {
            SQLiteDatabase database= this.openOrCreateDatabase("Memories",MODE_PRIVATE,null);
            Cursor cursor= database.rawQuery("SELECT * FROM memories",null);
            int nameIx=cursor.getColumnIndex("memoryname");
            int IdIx=cursor.getColumnIndex("id");

            while (cursor.moveToNext()){
                String name=cursor.getString(nameIx);
                int id=cursor.getInt(IdIx);
                Memory memory=new Memory(name,id);
                memoryArrayList.add(memory);
            }
            memoryAdapter.notifyDataSetChanged();
            cursor.close();

        }catch (Exception e){
            e.printStackTrace();

        }

        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.art_menu, menu);




        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addmemory) {
            Intent intent = new Intent(this, infoActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}

