package com.freak.videosenfants.app.browse.local;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.freak.videosenfants.R;
import com.freak.videosenfants.app.browse.BrowseActivity;
import com.freak.videosenfants.app.browse.BrowseAdapter;
import com.freak.videosenfants.domain.bean.VideoElement;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class BrowseLocalActivity extends BrowseActivity implements BrowseLocalContract.View {

    //private static final String TAG = BrowseLocalActivity.class.getSimpleName();

    @BindView(R.id.video_list)
    RecyclerView mListView;

    private BrowseAdapter mAdapter;
    //private Vector<File> mRoots;
    //private VideoElement mRootElement;

    //private final String[] mExtensions = {"avi" , "mkv", "wmv", "mpg", "mpeg", "mp4"};
    //private final Set<String> mSet = new HashSet<>(Arrays.asList(mExtensions));

    @Inject
    BrowseLocalContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_local);

        AndroidInjection.inject(this);
        ButterKnife.bind(this);

        mPresenter.subscribe(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(view -> BrowseLocalActivity.this.onBackPressed());

        getDialog().setContentView(R.layout.browse_sd_context_menu_layout);

        //mRootElement = new VideoElement(true, mRoot, mRoot, null, this);
        //mCurrent = mRootElement;
        mAdapter = new BrowseAdapter(mPresenter);

        mListView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(mAdapter);

        mPresenter.retrieveLocalRoots();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        /*mAdapter.clear();
        if (mCurrent.equals(mRootElement)) {
            addFilesToList(mRoots, mCurrent);
        }
        else {
            addFilesToList(new File(mCurrent.getPath()), mCurrent);
        }
        mAdapter.notifyDataSetChanged();*/
    }

    @Override
    public void onBackPressed() {
        mPresenter.goBack();
    }

    @Override
    protected void parseAndUpdate(VideoElement parent) {
        // ToDO delete method
    }

    @Override
    public void notifyElementsUpdated() {
        mAdapter.update();
    }

    @Override
    public void showElementThumbnail(VideoElement element, Uri uri) {
        mAdapter.updateThumbnail(element, uri);
    }

    /*private void addFilesToList(Vector<File> filesVector, VideoElement parent) {
        Vector<File> allFiles = new Vector<>();
        for (int i = 0 ; i < filesVector.size() ; i++) {
            File[] files = filesVector.get(i).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (pathname.isDirectory()) {
                        return true;
                    }
                    else {
                        try {
                            String extension = pathname.getName().substring(pathname.getName().lastIndexOf(".") + 1).toLowerCase();
                            return mSet.contains(extension);
                        }
                        catch (Exception e) {
                            return false;
                        }
                    }
                }
            });
            allFiles.addAll(new HashSet<>(Arrays.asList(files)));
        }
        Vector<VideoElement> vector = sortFiles(allFiles, parent);
        mAdapter.clear();
        mAdapter.addAll(vector);
    }*/

    /*private void addFilesToList(File file, VideoElement parent) {
        Vector<File> fileVector = new Vector<>();
        fileVector.add(file);
        addFilesToList(fileVector, parent);
    }

    private Vector<VideoElement> sortFiles(Vector<File> entries, VideoElement parent) {
        Vector<VideoElement> directories = new Vector<>();
        Vector<VideoElement> files = new Vector<>();

        for (int i = 0; i < entries.size() ; i++) {
            if (entries.get(i).isDirectory()) {
                directories.add(new VideoElement(entries.get(i), parent, this));
            } else {
                files.add(new VideoElement(entries.get(i), parent, this, mListView));
            }
        }

        Log.i(TAG, "" + directories.size() + " directories found");
        Log.i(TAG, "" + files.size() + " files found");

        sort(directories);
        sort(files);

        Log.i(TAG, "" + directories.size() + " directories after sorting");
        Log.i(TAG, "" + files.size() + " files after sorting");

        Vector<VideoElement> allFiles = new Vector<>();
        for (int i = 0; i < directories.size(); i++) {
            allFiles.add(directories.get(i));
        }
        for (int i = 0; i < files.size(); i++) {
            allFiles.add(files.get(i));
        }
        return allFiles;
    }

    private void sort (Vector<VideoElement> files) {
        int longueur = files.size();
        VideoElement tampon;
        boolean permut;

        do {
            // hypothèse : le tableau est trié
            permut = false;
            for (int i = 0; i < longueur - 1; i++) {
                // Teste si 2 éléments successifs sont dans le bon ordre ou non
                if (files.get(i).getName().compareToIgnoreCase(files.get((i+1)).getName()) > 0) {
                    // s'ils ne le sont pas, on échange leurs positions
                    tampon = files.get(i);
                    files.set(i, files.get(i + 1));
                    files.set(i+1, tampon);
                    permut = true;
                }
            }
        } while (permut);
    }*/

    /*@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VideoElement element = mAdapter.getItem(position);
        if (element.isDirectory()) {
            mCurrent = element;
            addFilesToList(new File(mCurrent.getPath()), mCurrent);
            mAdapter.notifyDataSetChanged();
            mListView.setSelectionAfterHeaderView();
        }
        else {
            mPresenter.playVideo(Uri.parse(element.getPath()));
        }
    }*/

    /*protected void parseAndUpdate(VideoElement element) {
        if (element.getPath().equals(mRoot)) {
            Log.i(TAG, "Going to root");
            mCurrent = mRootElement;
            for (int i = 0 ; i < mRoots.size() ; i++) {
                addFilesToList(mRoots, mRootElement);
            }
        }
        else {
            Log.i(TAG, "Going to " + mCurrent.getParent().getPath());
            mCurrent = element;
            addFilesToList(new File(mCurrent.getPath()), mCurrent);
            mAdapter.notifyDataSetChanged();
            mListView.setSelectionAfterHeaderView();
        }
        mAdapter.notifyDataSetChanged();
        mListView.setSelectionAfterHeaderView();
    }*/

    /*@Override
    protected void prepareContextMenu(final AdapterView<?> parent, final int position) {
        super.prepareContextMenu(parent, position);
        Button deleteButton = (Button) getDialog().findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(BrowseLocalActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(BrowseLocalActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            ApplicationSingleton.MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

                }

                final VideoElement element = mAdapter.getItem(position);
                Log.i(TAG, "Delete " + element.getPath());
                getDialog().dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(BrowseLocalActivity.this);
                builder.setTitle(getString(R.string.delete_title));
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(element.getPath());
                        if (!file.isDirectory() || file.listFiles().length == 0) {
                            if (file.delete()) {
                                mAdapter.remove(element);
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BrowseLocalActivity.this);
                            builder.setTitle(getString(R.string.delete_dir_title));
                            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }*/

}
