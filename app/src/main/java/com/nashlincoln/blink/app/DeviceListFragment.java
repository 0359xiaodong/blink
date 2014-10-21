package com.nashlincoln.blink.app;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nashlincoln.blink.R;
import com.nashlincoln.blink.content.DeviceLoader;
import com.nashlincoln.blink.model.Syncro;
import com.nashlincoln.blink.model.Device;

import java.util.List;

/**
 * Created by nash on 10/19/14.
 */
public class DeviceListFragment extends Fragment {
    private static final String TAG = "DeviceListFragment";
    private ListView mListView;
    private DeviceAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = new ListView(inflater.getContext());
        return mListView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new DeviceAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        getLoaderManager().restartLoader(0, null, mLoaderCallbacks);
    }

    private LoaderManager.LoaderCallbacks<List<Device>> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<Device>>() {
        @Override
        public Loader<List<Device>> onCreateLoader(int i, Bundle bundle) {
            Log.d(TAG, "onCreateLoader");
            return new DeviceLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<List<Device>> listLoader, List<Device> devices) {
            Log.d(TAG, "onLoadFinished");
            mAdapter.clear();
            if (devices != null) {
                mAdapter.addAll(devices);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Device>> listLoader) {
            Log.d(TAG, "onLoaderReset");
        }
    };

    class DeviceAdapter extends ArrayAdapter<Device> {

        public DeviceAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.device_item, null);
                holder = new Holder(convertView);
            } else {
                holder = (Holder) convertView.getTag();
            }

            bindView(position, holder);

            return convertView;
        }

        private void bindView(int position, Holder holder) {
            Device device = getItem(position);
            holder.position = position;
            holder.textView.setText(device.getName());
            holder.checkBox.setChecked(device.isOn());
            holder.seekBar.setProgress(device.getLevel());
        }

        class Holder implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {
            int position;
            TextView textView;
            CheckBox checkBox;
            SeekBar seekBar;

            Holder(View view) {
                view.setTag(this);
                textView = (TextView) view.findViewById(R.id.text);
                checkBox = (CheckBox) view.findViewById(R.id.check_box);
                seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
                seekBar.setMax(255);
                checkBox.setOnCheckedChangeListener(this);
                seekBar.setOnSeekBarChangeListener(this);
            }

            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, boolean b) {
                Device device = getItem(position);
                device.setOn(b);
                Syncro.getInstance().syncDevices();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Device device = getItem(position);
                device.setLevel(seekBar.getProgress());
                Syncro.getInstance().syncDevices();
            }
        }
    }
}
