package com.freak.videosenfants.elements.preferences;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.freak.videosenfants.R;

import java.util.List;

public class DlnaAdapter extends ArrayAdapter<DlnaElement> {

    public DlnaAdapter(Context context, List<DlnaElement> elements) {
        super(context, 0, elements);
    }

    public DlnaAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.file_element, parent, false);
        }

        DlnaHolder viewHolder = (DlnaHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new DlnaHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.layout);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        DlnaElement element = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.name.setText(element.getName());

        int padding = viewHolder.layout.getPaddingRight();
        viewHolder.layout.setPadding(
                padding * (element.getIndent() + 1),
                padding, padding, padding);

        return convertView;
    }

    private class DlnaHolder{
        public TextView name;
        public RelativeLayout layout;
    }

}