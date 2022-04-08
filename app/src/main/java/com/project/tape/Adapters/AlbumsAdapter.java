package com.project.tape.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.project.tape.R;
import com.project.tape.SecondaryClasses.Song;

import java.io.IOException;
import java.util.ArrayList;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder> {

    private Context mContext;
    public static ArrayList<Song> mAlbumList;
    private OnAlbumListener onAlbumListener;
    private Uri uri;
    MediaMetadataRetriever retriever = new MediaMetadataRetriever();

    byte[] art;


    public AlbumsAdapter(Context mContext, ArrayList<Song> mAlbumList, OnAlbumListener onAlbumListener) {
        this.mContext = mContext;
        this.mAlbumList = mAlbumList;
        this.onAlbumListener = onAlbumListener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.album_item, parent, false);
        AlbumViewHolder vHolder = new AlbumViewHolder(v, onAlbumListener);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.tv_album_title.setText(mAlbumList.get(position).getAlbum());
        uri = Uri.parse(mAlbumList.get(position).getData());
        retriever.setDataSource(uri.toString());
        art = retriever.getEmbeddedPicture();
        if (art != null) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(art)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .override(100, 100)
                    .placeholder(R.drawable.default_cover)
                    .into(holder.album_cover_albumFragment);
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(R.drawable.default_cover)
                    .override(100, 100)
                    .into(holder.album_cover_albumFragment);
        }
    }

    @Override
    public void onViewRecycled(@NonNull AlbumViewHolder holder) {
        Glide.with(mContext).clear(holder.album_cover_albumFragment);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }


    public class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_album_title;
        ImageView album_cover_albumFragment;
        OnAlbumListener onAlbumListener;

        public AlbumViewHolder(@NonNull View itemView, OnAlbumListener onAlbumListener) {
            super(itemView);
            this.onAlbumListener = onAlbumListener;
            tv_album_title = (TextView) itemView.findViewById(R.id.album_title_albumFragment);
            album_cover_albumFragment = (ImageView) itemView.findViewById(R.id.album_cover_albumFragment);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                onAlbumListener.onAlbumClick(getAdapterPosition());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void updateAlbumList(ArrayList<Song> albumsArrayList) {
        mAlbumList = new ArrayList<>();
        mAlbumList.addAll(albumsArrayList);
        notifyDataSetChanged();
    }


    public interface OnAlbumListener {
        void onAlbumClick(int position) throws IOException;
    }


}

