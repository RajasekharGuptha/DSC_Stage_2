package com.dsc.recyclerviewUtils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsc.R;

import java.util.ArrayList;
import java.util.List;

public class home_rv_adapter extends RecyclerView.Adapter<home_rv_adapter.viewholder> implements Filterable {

    final private listItemClickListener onClickListener;
    private Context context;
    private ArrayList<home_rv_item> detailsArrayList;
    private ArrayList<home_rv_item> detailsArrayListfiltered;

    // search filtering
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null && constraint.length() > 0) {
                    List<home_rv_item> filterList = new ArrayList<>();
                    for (home_rv_item iterItem : detailsArrayList) {
                        if ((iterItem.getText().toLowerCase().contains(constraint.toString().toLowerCase())))
                        {
                            filterList.add(iterItem);
                            Log.e("TAG", "performFiltering: "+constraint +iterItem.getText());
                        }
                    }
                    results.count = filterList.size();
                    results.values = filterList;
                } else {
                    results.count = detailsArrayListfiltered.size();
                    results.values = detailsArrayListfiltered;
                }
                return results;
            }

                @Override
                protected void publishResults (CharSequence constraint, FilterResults results){
                    detailsArrayList = (ArrayList<home_rv_item>) results.values;
                    notifyDataSetChanged();
                }
            };

        }

        public interface listItemClickListener {
            void onListItemClick(int clickInt);
        }


    public
    home_rv_adapter(Context context, ArrayList < home_rv_item > detailsArrayList, listItemClickListener itemClickListener)
        {
            this.context = context;
            this.detailsArrayList = detailsArrayList;
            this.detailsArrayListfiltered = detailsArrayList;
            this.onClickListener = itemClickListener;
        }

        @NonNull
        @Override
        public viewholder onCreateViewHolder (@NonNull ViewGroup parent,int viewType){

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.recycler_view_item_layout, parent, false);

            return new viewholder(view);

        }

        @Override
        public void onBindViewHolder ( @NonNull final viewholder holder, final int position){

            holder.text.setText(detailsArrayList.get(position).getText());

        }

        @Override
        public int getItemCount () {
            return detailsArrayList.size();
        }

        class viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView text;

            public viewholder(@NonNull View itemView) {
                super(itemView);
                this.text = itemView.findViewById(R.id.rv_item);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                onClickListener.onListItemClick(getAdapterPosition());
            }
        }
    }
