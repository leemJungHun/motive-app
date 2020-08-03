package kr.rowan.motive_app.adapter;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

import kr.rowan.motive_app.data.OpenSourceItem;
import kr.rowan.motive_app.databinding.ItemOpenLicenceBinding;


public class OpenSourceListAdapter extends RecyclerView.Adapter<OpenSourceListAdapter.OpenSourceViewHolder> {

    private Vector<OpenSourceItem> items;

    @NonNull
    @Override
    public OpenSourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOpenLicenceBinding binding = ItemOpenLicenceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OpenSourceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OpenSourceViewHolder holder, int position) {
        ItemOpenLicenceBinding binding = holder.binding;
        OpenSourceItem item = items.get(position);

        binding.title.setText(item.getName());
        binding.url.setText(item.getUrl());
        binding.copyRighter.setText(item.getCopyRighter());
        binding.organization.setText(item.getOrganization());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(Vector<OpenSourceItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    static class OpenSourceViewHolder extends RecyclerView.ViewHolder {

        private ItemOpenLicenceBinding binding;

        public OpenSourceViewHolder(ItemOpenLicenceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}
