package com.example.rentalz.ViewHolder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.rentalz.Models.Property;
import com.example.rentalz.R;
import java.util.ArrayList;
import java.util.List;


public class PropertyListAdapter extends BaseAdapter {
    Context context;
    private int layout;
    ArrayList<Property> propertyList;
    ArrayList<Property> propertyFiltered;

    public PropertyListAdapter(Context context, int layout, ArrayList<Property> propertyList )
    {
        this.context = context;
        this.layout = layout;
        this.propertyList = propertyList;
        this.propertyFiltered = propertyList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return propertyFiltered.size();
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String Key = charSequence.toString();

                if (Key.isEmpty())
                {
                    propertyFiltered= propertyList;
                }
                else
                {
                    List<Property> isFiltered = new ArrayList<>();
                    for (Property row : propertyList)
                    {
                        if (row.getPropertyType().toLowerCase().contains(Key.toLowerCase()) || row.getReporterName().toLowerCase().contains(Key.toLowerCase()))
                        {
                            isFiltered.add(row);
                        }
                    }

                    propertyFiltered = (ArrayList<Property>) isFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = propertyFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                propertyFiltered = (ArrayList<Property>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public Object getItem(int i) {
        return propertyList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder
    {
        ImageView imageView;
        TextView propertyType, bedrooms, date, time, monthlyRentPrice, furnitureType, notes, addNotes, repName;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row==null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.imageView = row.findViewById(R.id.prop_image);
            holder.propertyType = row.findViewById(R.id.txt_prop_type);
            holder.bedrooms = row.findViewById(R.id.txt_prop_bedrooms);
            holder.date = row.findViewById(R.id.visit_date);
            holder.time = row.findViewById(R.id.visit_time);
            holder.monthlyRentPrice = row.findViewById(R.id.txt_rent_price);
            holder.furnitureType = row.findViewById(R.id.txt_furn_type);
            holder.notes = row.findViewById(R.id.txt_notes);
//          holder.addNotes = row.findViewById(R.id.add_notes);
            holder.repName = row.findViewById(R.id.rep_name);
            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)row.getTag();
        }

        Property model = propertyList.get(i);

        byte[] resImage = model.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(resImage, 0, resImage.length);

        holder.imageView.setImageBitmap(bitmap);
        //holder.imageView.findViewById(R.id.add_prop_image);
        holder.propertyType.setText(model.getPropertyType());
        holder.bedrooms.setText(model.getBedrooms());
        holder.date.setText(model.getDate());
        holder.time.setText(model.getTime());
        holder.monthlyRentPrice.setText(model.getMonthlyRentPrice());
        holder.furnitureType.setText(model.getFurnitureType());
        holder.notes.setText(model.getNotes());
//      holder.addNotes.setText(model.getAddNotes());
        holder.repName.setText(model.getReporterName());

        return row;
    }
}
