package com.berdanbakan.photocloud;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.berdanbakan.photocloud.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryHolder> {
    ArrayList<Memory> memoryArrayList;

    public MemoryAdapter(ArrayList<Memory> memoryArrayList){
        this.memoryArrayList=memoryArrayList;
    }

    @NonNull
    @Override
    public MemoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding= RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MemoryHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryHolder holder, int position) {
        holder.binding.recyclerViewText.setText(memoryArrayList.get(position).name);

    }

    @Override
    public int getItemCount() {
        return memoryArrayList.size();
    }

    public class MemoryHolder extends RecyclerView.ViewHolder{
        private RecyclerRowBinding binding;

        public MemoryHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
