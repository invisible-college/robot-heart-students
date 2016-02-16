package college.invisible.robothello;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ppham on 2/8/16.
 */
public class SampleRecyclerAdapter extends RecyclerView.Adapter<SampleRecyclerAdapter.ViewHolder>
        implements RecyclerView.OnClickListener {
    private final List<SampleModel> sampleData = new ArrayList<>();

    public SampleRecyclerAdapter(RecyclerView rv) {
        sampleData.add(
            new SampleModel("What do you call an alligator in a trench coat?","An investigator")
        );

        sampleData.add(
            new SampleModel("What do you call a kitten on a hill?","A meowntain.")
        );

        rv.setAdapter(this);
        rv.setOnClickListener(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parentViewGroup, int i) {

        View rowView = LayoutInflater.from(parentViewGroup.getContext())
                .inflate(R.layout.list_basic_item, parentViewGroup, false);

        return new ViewHolder (rowView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        final SampleModel rowData = sampleData.get(position);
        viewHolder.textViewSample.setText(rowData.getQuestion());

        viewHolder.itemView.setTag(rowData);
    }

    @Override
    public int getItemCount() {
        return sampleData.size();
    }

    public void removeData(int position) {
        sampleData.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(int positionToAdd) {
        if (positionToAdd == -1) {
            positionToAdd = 0;
        }
        SampleModel sampleModel = new SampleModel("What is the meaning of life, the universe, and everything?", "42");
        sampleModel.saveInBackground();
        sampleData.add(positionToAdd, sampleModel);
        notifyItemInserted(positionToAdd);

    }

    public void addItems(List<SampleModel> items) {
        sampleData.addAll(items);
        notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        private final TextView textViewSample;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewSample = (TextView) itemView.findViewById(
                    R.id.textViewSample);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // v is the LinearLayout, it turns out, the parent.
            int position = this.getLayoutPosition();
            SampleModel data = (SampleModel) this.itemView.getTag();
            data.toggleSide();
            textViewSample.setText(data.getVisibleString());
            //Log.d("Tag1", "At position + " + Integer.toString(position) + " " + v.toString());
        }
    }

}
