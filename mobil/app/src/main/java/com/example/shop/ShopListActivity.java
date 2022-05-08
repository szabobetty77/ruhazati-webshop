package com.example.shop;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ShopListActivity extends AppCompatActivity {
    private final static String LOG_TAG = ShopListActivity.class.getName();
    private FirebaseUser user;

    private RecyclerView fRecyclerView;
    private ArrayList<ShoppingItem> fItemList;
    private ShoppingItemAdapter fAdapter;

    private FirebaseFirestore fFirestore;
    private CollectionReference fItems;

    private NotificationHandler fNotificationHandler;

    private FrameLayout redCircle;
    private TextView contentTextView;

    private int gridNumber = 1;
    private boolean viewRow = true;
    private int cartItems = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            Log.d(LOG_TAG, "Authenticated user!");
        }else{
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        fRecyclerView = findViewById(R.id.recyclerView);
        fRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        fItemList = new ArrayList<>();

        fAdapter = new ShoppingItemAdapter(this, fItemList);
        fRecyclerView.setAdapter(fAdapter);

        fFirestore = FirebaseFirestore.getInstance();
        fItems = fFirestore.collection("Items");

        queryData();

        fNotificationHandler = new NotificationHandler(this);

    }

    private void queryData(){
        fItemList.clear();

        //fItems.whereEqualTo()..
        fItems.orderBy("cartCount", Query.Direction.DESCENDING).limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                ShoppingItem item = document.toObject(ShoppingItem.class);
                item.setId(document.getId());
                fItemList.add(item);
            }
            if(fItemList.size() == 0) {
                initializeData();
                queryData();
            }
            fAdapter.notifyDataSetChanged();
        });

    }

    public void deleteItem(ShoppingItem item){
        DocumentReference r = fItems.document(item._getId());
        r.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Item is successfully deleted: " + item._getId());
        })
                .addOnFailureListener(failure -> {
                    Toast.makeText(this, "Item " + item._getId() + " cannot be deleted", Toast.LENGTH_LONG).show();
                });
        queryData();
    }


    private void initializeData() {
        String[] itemsList = getResources().getStringArray(R.array.shopping_item_names);
        String[] itemsInfo = getResources().getStringArray(R.array.shopping_item_desc);
        String[] itemsPrice = getResources().getStringArray(R.array.shopping_item_price);

        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.shopping_item_images);
        TypedArray itemsRating = getResources().obtainTypedArray(R.array.shopping_item_rates);

        for (int i = 0; i < itemsList.length; i++)
            fItems.add(new ShoppingItem(
                    itemsList[i],
                    itemsInfo[i],
                    itemsPrice[i],
                    itemsRating.getFloat(i, 0),
                    itemsImageResource.getResourceId(i, 0), 0));


        itemsImageResource.recycle();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                fAdapter.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out_button:
                Log.d(LOG_TAG, "Log out clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.cart:
                Log.d(LOG_TAG, "Cart clicked!");
                return true;
            case R.id.view_selector:
                Log.d(LOG_TAG, "View selector clicked!");
                if(viewRow){
                    changeSpanCount(item, R.drawable.ic_view_grid, 1);
                }else{
                    changeSpanCount(item, R.drawable.ic_view_row, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) fRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }
    public void updateAlertIcon(ShoppingItem item){
        cartItems = (cartItems + 1);
        if(cartItems > 0){
            contentTextView.setText(String.valueOf(cartItems));
        }else{
            contentTextView.setText("");
        }
        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);

        fItems.document(item._getId()).update("cartCount", item.getCartCount() + 1).addOnFailureListener(failure -> {
            Toast.makeText(this, "Item " + item._getId() + " cannot be changed", Toast.LENGTH_LONG).show();
        });

        fNotificationHandler.send(item.getName());
        queryData();
    }
}