package com.example.madcamp_week1;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DictionaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final int HEADER = 0;
    public static final int CHILD = 1;
    private ArrayList<Item> data;
    private OnItemClickListener mListner;

    // constructor
    public DictionaryAdapter(ArrayList<Item> list){
        this.data = list;
    }

    public class DictionaryViewHolder extends RecyclerView.ViewHolder {

        protected TextView index;
        protected TextView name;
        protected TextView contact;

        public DictionaryViewHolder(@NonNull @NotNull View itemView, OnItemClickListener listener) {
            super(itemView);

            this.index = (TextView) itemView.findViewById(R.id.index_id);
            this.name = (TextView) itemView.findViewById(R.id.name_id);
            this.contact = (TextView) itemView.findViewById(R.id.contact_id);

            this.contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    private static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView category;
        public ImageView btn_expand_toggle;
        public Item refferalItem;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            category = (TextView) itemView.findViewById(R.id.category_id);
            btn_expand_toggle = (ImageView) itemView.findViewById(R.id.btn_expand_toggle_id);
        }
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {
            case HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_header, parent, false);
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);
                return header;
            case CHILD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
                DictionaryViewHolder child = new DictionaryViewHolder(view, mListner);
                return child;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        final Item item = data.get(position);
        switch (item.type) {
            case HEADER:
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
                itemController.refferalItem = item;
                itemController.category.setText(item.category);
                if (item.invisibleChildren == null) {
                    itemController.btn_expand_toggle.setImageResource(R.mipmap.circle_minus);
                } else {
                    itemController.btn_expand_toggle.setImageResource(R.mipmap.circle_plus);
                }
                itemController.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.invisibleChildren == null) {
                            item.invisibleChildren = new ArrayList<Item>();
                            int count = 0;
                            int pos = data.indexOf(itemController.refferalItem);
                            while (data.size() > pos + 1 && data.get(pos + 1).type == CHILD) {
                                item.invisibleChildren.add(data.remove(pos + 1));
                                count++;
                            }
                            notifyItemRangeRemoved(pos + 1, count);
                            itemController.btn_expand_toggle.setImageResource(R.mipmap.circle_plus);
                        } else {
                            int pos = data.indexOf(itemController.refferalItem);
                            int index = pos + 1;
                            for (Item i : item.invisibleChildren) {
                                data.add(index, i);
                                index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);
                            itemController.btn_expand_toggle.setImageResource(R.mipmap.circle_minus);
                            item.invisibleChildren = null;
                        }
                    }
                });
                break;

            case CHILD:
                DictionaryViewHolder childitemController = (DictionaryViewHolder) holder;
                childitemController.index.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                childitemController.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                childitemController.contact.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

                childitemController.index.setGravity(Gravity.CENTER);
                childitemController.name.setGravity(Gravity.CENTER);
                childitemController.contact.setGravity(Gravity.CENTER);

                childitemController.index.setText(item.dict.getIndex());
                childitemController.name.setText(item.dict.getName());
                childitemController.contact.setText(item.dict.getContact());

        }
    }

    @Override
    public int getItemCount() {
        return (null != data ? data.size() : 0);
    }

    public int getItemViewType(int position) {
        return data.get(position).type;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListner = listener;
    }
}
