package kr.rowan.motive_app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Vector;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.adapter.OpenSourceListAdapter;
import kr.rowan.motive_app.data.OpenSourceItem;
import kr.rowan.motive_app.databinding.FragmentOpenSourceLicenceBinding;

public class OpenSourceLicenceFragment extends Fragment implements View.OnClickListener{
    FragmentOpenSourceLicenceBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_open_source_licence, container, false);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        OpenSourceListAdapter adapter = new OpenSourceListAdapter();
        binding.ossListView.setLayoutManager(manager);
        binding.ossListView.setAdapter(adapter);

        OpenSourceItem commons_io = new OpenSourceItem(
                "commons-io", "https://github.com/apache/commons-io", "Copyright © 2002-2020 The Apache Software Foundation", "Apache License 2.0"
        );
        OpenSourceItem glide = new OpenSourceItem(
                "glide", "https://github.com/bumptech/glide", "Sam Judd - @sjudd on GitHub, @samajudd on Twitter", "BSD, part MIT and Apache License 2.0"
        );
        OpenSourceItem gson = new OpenSourceItem(
                "gson", "https://github.com/google/gson", "Copyright 2008 Google Inc.", "Apache License 2.0"
        );
        OpenSourceItem jsoup = new OpenSourceItem(
                "jsoup", "https://github.com/jhy/jsoup/", "Copyright � 2009 - 2020 Jonathan Hedley (https://jsoup.org/)", "MIT License"
        );
        OpenSourceItem retrofit = new OpenSourceItem(
                "retrofit", "https://github.com/square/retrofit", "Copyright 2013 Square, Inc.", "Apache License 2.0"
        );
        OpenSourceItem jBCrypt = new OpenSourceItem(
                "jBCrypt", "https://www.mindrot.org/projects/jBCrypt/", "Copyright (c) 2006 Damien Miller <djm@mindrot.org>", "ISC License"
        );
        OpenSourceItem ShortcutBadger = new OpenSourceItem(
                "ShortcutBadger", "https://github.com/leolin310148/ShortcutBadger", "Copyright 2014 Leo Lin", "Apache License 2.0"
        );
        OpenSourceItem SiliCompressor = new OpenSourceItem(
                "SiliCompressor", "https://github.com/Tourenathan-G5organisation/SiliCompressor", "Copyright 2016 Teyou Toure Nathan", "Apache License 2.0"
        );
        OpenSourceItem tedPermission = new OpenSourceItem(
                "TedPermission", "https://github.com/ParkSangGwon/TedPermission", "Copyright 2017 Ted Park", "Apache License 2.0"
        );


        Vector<OpenSourceItem> items = new Vector<>();
        items.add(commons_io);
        items.add(gson);
        items.add(glide);
        items.add(jsoup);
        items.add(retrofit);
        items.add(jBCrypt);
        items.add(ShortcutBadger);
        items.add(SiliCompressor);
        items.add(tedPermission);

        adapter.setData(items);

        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {

    }
}
