package com.sahilpaudel.app.suggme.foundsomethingnew;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sahilpaudel.app.suggme.R;
import com.yalantis.flipviewpager.adapter.BaseFlipAdapter;
import com.yalantis.flipviewpager.utils.FlipSettings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.sahilpaudel.app.suggme.foundsomethingnew.Utils.friends;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoundSomethingNew extends Fragment {

    View view;

    ListView listView;

    public FoundSomethingNew() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_found_something_new, container, false);
        listView = (ListView) view.findViewById(R.id.something_new);
        FlipSettings settings = new FlipSettings.Builder().defaultPage(1).build();
        listView.setAdapter(new FoundNewAdapter(getActivity(), friends, settings));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Found f = (Found) listView.getAdapter().getItem(position);

                Toast.makeText(getActivity(), f.getNickname(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    class FoundNewAdapter extends BaseFlipAdapter {

        private final int PAGES = 3;

        public FoundNewAdapter(Context context, List<Found> items, FlipSettings settings) {
            super(context, items, settings);
        }

        @Override
        public View getPage(int position, View convertView, ViewGroup parent, Object friend1, Object friend2) {
            final FriendsHolder holder;

            if (convertView == null) {
                holder = new FriendsHolder();
                convertView = getActivity().getLayoutInflater().inflate(R.layout.two_merge_page, parent, false);
                holder.leftAvatar = (ImageView) convertView.findViewById(R.id.first);
                holder.rightAvatar = (ImageView) convertView.findViewById(R.id.second);
                holder.infoPage = getActivity().getLayoutInflater().inflate(R.layout.something_new_info, parent, false);
                holder.foundTitle = (TextView) holder.infoPage.findViewById(R.id.nickname);
                holder.foundContent = (TextView) holder.infoPage.findViewById(R.id.something_new_content);

                convertView.setTag(holder);
            } else {
                holder = (FriendsHolder) convertView.getTag();
            }

            switch (position) {
                // Merged page with 2 friends
                case 1:
                    holder.leftAvatar.setImageResource(((Found) friend1).getAvatar());
                    if (friend2 != null)
                        holder.rightAvatar.setImageResource(((Found) friend2).getAvatar());
                    break;
                default:
                    fillHolder(holder, position == 0 ? (Found) friend1 : (Found) friend2);
                    holder.infoPage.setTag(holder);
                    return holder.infoPage;
            }
            return convertView;
        }


        @Override
        public int getPagesCount() {
            return PAGES;
        }

        private void fillHolder(FriendsHolder holder, Found found) {
            if (found == null)
                return;
            holder.foundTitle.setText(found.getNickname());
            holder.foundContent.setText(found.getDescription());
        }

        class FriendsHolder {
            ImageView leftAvatar;
            ImageView rightAvatar;
            View infoPage;

            TextView foundTitle;
            TextView foundContent;
        }
    }

}
