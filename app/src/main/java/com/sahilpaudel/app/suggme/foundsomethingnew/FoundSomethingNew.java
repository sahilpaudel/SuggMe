package com.sahilpaudel.app.suggme.foundsomethingnew;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.sahilpaudel.app.suggme.Config;
import com.sahilpaudel.app.suggme.R;
import com.sahilpaudel.app.suggme.SharedPrefSuggMe;
import com.sahilpaudel.app.suggme.location.GetUserAddress;
import com.squareup.picasso.Picasso;
import com.yalantis.flipviewpager.adapter.BaseFlipAdapter;
import com.yalantis.flipviewpager.utils.FlipSettings;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoundSomethingNew extends Fragment {

    View view;

    ListView listView;

    Button buttonAddNew;

    String state;

    Found foundInfo;
    List<Found> list;

    EditText foundLocation;
    ImageView foundImage;
    StringRequest FoundRequest;

    String foundAddress;

    private final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    //Image request code
    private int PICK_IMAGE_REQUEST = 2;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;

    public FoundSomethingNew() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_found_something_new, container, false);
        buttonAddNew =(Button)view.findViewById(R.id.addButton);

        buttonAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewShow("","","CREATE");
            }
        });
        list = new ArrayList<>();
        GetUserAddress getUserAddress = new GetUserAddress(getActivity());
        getUserAddress.executeGPS();

        state = getUserAddress.getState();
        final ProgressDialog progress = ProgressDialog.show(getActivity(), "Please wait...", "Loading contents...", false, false);
        FoundRequest = new StringRequest(Request.Method.POST, Config.URL_GET_SOMETHING_NEW, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray array = new JSONArray(response);
                    for (int i =0 ; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String found_image = object.getString("found_image");
                        String found_date = object.getString("entryOn");
                        String found_content = object.getString("content");
                        String found_title = object.getString("title");
                        foundInfo = new Found(found_image, found_title, found_content,found_date,"yes","no");
                        list.add(foundInfo);
                    }
                    listView = (ListView) view.findViewById(R.id.something_new);
                    FlipSettings settings = new FlipSettings.Builder().defaultPage(1).build();
                    listView.setAdapter(new FoundNewAdapter(getActivity(), list, settings));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                           // Found f = (Found) listView.getAdapter().getItem(position);
                            Toast.makeText(getActivity(), parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("STATE",state);
                return params;
            }
        };
        FoundRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue =Volley.newRequestQueue(getActivity());

        queue.add(FoundRequest);

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
                holder.foundFirstTitle = (TextView) convertView.findViewById(R.id.foundFirstTitle);
                holder.foundSecondTitle = (TextView) convertView.findViewById(R.id.foundSecondTitle);
                holder.infoPage = getActivity().getLayoutInflater().inflate(R.layout.something_new_info, parent, false);
                holder.foundTitle = (TextView) holder.infoPage.findViewById(R.id.nickname);
                holder.foundContent = (TextView) holder.infoPage.findViewById(R.id.something_new_content);


                convertView.setTag(holder);
            } else {
                holder = (FriendsHolder) convertView.getTag();
            }

            switch (position) {
                // Merged page with 2 contents
                case 1:
                        Picasso.with(getActivity()).load(((Found) friend1).getAvatar()).into(holder.leftAvatar);
                        holder.foundFirstTitle.setText(((Found)friend1).getNickname());

                    if (friend2 != null) {
                        Picasso.with(getActivity()).load(((Found) friend2).getAvatar()).into(holder.rightAvatar);
                        holder.foundSecondTitle.setText(((Found) friend2).getNickname());
                    }
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

        private void fillHolder(FriendsHolder holder, final Found found) {
            if (found == null)
                return;
            holder.foundTitle.setText(found.getNickname());
            holder.foundContent.setText(found.getDescription());

        }

        class FriendsHolder {
            ImageView leftAvatar;
            ImageView rightAvatar;

            ImageView imageEdit1;
            ImageView imageEdit2;

            TextView foundFirstTitle;
            TextView foundSecondTitle;
            View infoPage;

            TextView foundTitle;
            TextView foundContent;
            Button btYes, btNo;
        }
    }

    Intent intent;

    @SuppressLint("CutPasteId")
    private void addNewShow(String title, String description, final String str) {

        AlertDialog.Builder addNew = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertView = inflater.inflate(R.layout.addnewview, null);

        final EditText foundTitle = (EditText)alertView.findViewById(R.id.addNewTitle);
        final EditText foundDescription = (EditText)alertView.findViewById(R.id.addNewDescription);
        foundLocation = (EditText)alertView.findViewById(R.id.addNewLocation);
        Button chooseImage = (Button) alertView.findViewById(R.id.addNewImage);
        Button btSubmit = (Button)alertView.findViewById(R.id.submitFound);
        foundImage = (ImageView)alertView.findViewById(R.id.foundImage);
        addNew.setView(alertView);
        final AlertDialog b = addNew.create();

        foundTitle.setText(title);
        foundDescription.setText(description);
        btSubmit.setText(str);

        //place autocomplete
        try {
            intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(getActivity());
            //startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        }catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        foundLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (foundTitle.getText().toString().isEmpty()) {
                    foundTitle.setError("It cannot be empty");
                }else if(foundLocation.getText().toString().isEmpty()){
                    foundLocation.setError("Select location");
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                }else if(foundDescription.getText().toString().isEmpty()){
                    foundDescription.setError("It cannot be empty");
                }else {
                    String title = foundTitle.getText().toString().trim();
                    String content = foundDescription.getText().toString().trim();
                    Toast.makeText(getActivity(), title+"/n"+content, Toast.LENGTH_SHORT).show();

                    if (str.equals("UPDATE")){
                        updateFoundNew(title, content);
                    }else {
                        uploadImage(title, content);
                    }
                    b.dismiss();
                }
            }
        });

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        b.show();
    }

    private void uploadImage(final String title, final String content){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Uploading...","Please wait...",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(getActivity(), s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getActivity(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters
                params.put("image", image);
                params.put("title", title);
                params.put("content", content);
                params.put("location", foundAddress);
                params.put("user_id", SharedPrefSuggMe.getInstance(getActivity()).getUserId());
                //returning parameters
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        queue.add(stringRequest);
    }

    private void updateFoundNew(final String title, final String description) {

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Updating...","Please wait...",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_UPLOAD_SOMETHING_NEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(getActivity(), s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getActivity(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters
                params.put("image", image);
                params.put("title", title);
                params.put("content", description);
                params.put("location", foundAddress);
                params.put("user_id", SharedPrefSuggMe.getInstance(getActivity()).getUserId());
                //returning parameters
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        queue.add(stringRequest);
    }

    //method to show file chooser
    private void showFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bmp){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                foundAddress = place.getAddress().toString();
                foundLocation.setText(foundAddress);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i("PLACE", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Toast.makeText(getActivity(), "status OK", Toast.LENGTH_SHORT).show();
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                foundImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Toast.makeText(getActivity(), ""+e, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
