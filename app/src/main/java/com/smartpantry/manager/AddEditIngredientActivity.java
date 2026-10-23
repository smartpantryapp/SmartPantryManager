package com.smartpantry.manager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.smartpantry.manager.database.PantryRepository;
import com.smartpantry.manager.models.Ingredient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etIngredientName;
    private EditText etIngredientQuantity;
    private EditText etIngredientExpiry;
    private Spinner spinnerIngredientUnit;
    private TextView tvFormTitle;
    private Button btnSaveIngredient;
    private Button btnCancelIngredient;

    private PantryRepository pantryRepository;

    private int ingredientId = -1;
    private boolean isEditMode = false;

    private final String[] units = {
            "piece", "pieces", "g", "kg", "ml", "l",
            "cup", "tbsp", "tsp", "clove", "cloves"
    };

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        View root = findViewById(R.id.addEditIngredientRoot);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars());

            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        pantryRepository = new PantryRepository(
                getApplicationContext());

        initialiseViews();
        setupUnitSpinner();
        setupExpiryDatePicker();
        readEditIntent();
        setupButtons();
    }

    private void initialiseViews() {
        tvFormTitle = findViewById(R.id.tvFormTitle);
        etIngredientName = findViewById(R.id.etIngredientName);
        etIngredientQuantity = findViewById(R.id.etIngredientQuantity);
        etIngredientExpiry = findViewById(R.id.etIngredientExpiry);
        spinnerIngredientUnit = findViewById(R.id.spinnerIngredientUnit);
        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);
        btnCancelIngredient = findViewById(R.id.btnCancelIngredient);
    }

    private void setupUnitSpinner() {
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinnerIngredientUnit.setAdapter(unitAdapter);
    }

    private void setupExpiryDatePicker() {
        etIngredientExpiry.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (datePicker, year, month, day) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, day);

                        etIngredientExpiry.setText(
                                dateFormat.format(selectedDate.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.setButton(
                    DatePickerDialog.BUTTON_NEUTRAL,
                    "Clear",
                    (dialog, which) -> etIngredientExpiry.setText("")
            );

            datePickerDialog.show();
        });
    }

    private void readEditIntent() {
        ingredientId = getIntent().getIntExtra("ingredient_id", -1);
        isEditMode = ingredientId != -1;

        if (!isEditMode) {
            return;
        }

        tvFormTitle.setText("Edit Ingredient");
        btnSaveIngredient.setText("Update Ingredient");

        etIngredientName.setText(
                getIntent().getStringExtra("ingredient_name"));

        etIngredientQuantity.setText(
                String.valueOf(
                        getIntent().getDoubleExtra("ingredient_quantity", 0)));

        etIngredientExpiry.setText(
                getIntent().getStringExtra("ingredient_expiry"));

        String savedUnit = getIntent().getStringExtra("ingredient_unit");

        if (savedUnit != null) {
            for (int i = 0; i < units.length; i++) {
                if (units[i].equalsIgnoreCase(savedUnit)) {
                    spinnerIngredientUnit.setSelection(i);
                    break;
                }
            }
        }
    }

    private void setupButtons() {
        btnSaveIngredient.setOnClickListener(view -> saveIngredient());

        btnCancelIngredient.setOnClickListener(view -> finish());
    }

    private void saveIngredient() {
        String name = etIngredientName.getText().toString().trim();
        String quantityText =
                etIngredientQuantity.getText().toString().trim();
        String unit = spinnerIngredientUnit.getSelectedItem().toString();
        String expiryDate =
                etIngredientExpiry.getText().toString().trim();

        if (name.isEmpty()) {
            etIngredientName.setError("Enter an ingredient name");
            etIngredientName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            etIngredientQuantity.setError("Enter a quantity");
            etIngredientQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(
                    quantityText.replace(',', '.'));
        } catch (NumberFormatException e) {
            etIngredientQuantity.setError(
                    "Enter a valid number, for example 2 or 2.5");
            etIngredientQuantity.requestFocus();
            return;
        }

        if (!Double.isFinite(quantity) || quantity <= 0) {
            etIngredientQuantity.setError(
                    "Quantity must be greater than zero");
            etIngredientQuantity.requestFocus();
            return;
        }

        if (!expiryDate.isEmpty() && !isValidDate(expiryDate)) {
            etIngredientExpiry.setError(
                    "Select a valid expiry date");
            return;
        }

        try {
            Ingredient ingredient = new Ingredient(
                    name,
                    quantity,
                    unit,
                    expiryDate.isEmpty() ? null : expiryDate
            );

            long result;

            if (isEditMode) {
                ingredient.setId(ingredientId);

                int rowsUpdated =
                        pantryRepository.updateIngredient(ingredient);

                result = rowsUpdated > 0 ? ingredientId : -1;
            } else {
                result = pantryRepository.addIngredient(ingredient);
            }

            if (result != -1) {
                Toast.makeText(
                        this,
                        isEditMode
                                ? "Ingredient updated successfully."
                                : "Ingredient saved successfully.",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            } else {
                Toast.makeText(
                        this,
                        "Unable to save the ingredient. Please try again.",
                        Toast.LENGTH_LONG
                ).show();
            }

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "An error occurred while saving the ingredient.",
                    Toast.LENGTH_LONG
            ).show();

            android.util.Log.e(
                    "SmartPantry",
                    "Unable to save ingredient",
                    e
            );
        }
    }

    private boolean isValidDate(String dateText) {
        dateFormat.setLenient(false);

        try {
            Date parsedDate = dateFormat.parse(dateText);
            return parsedDate != null;
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        if (pantryRepository != null) {
            pantryRepository.close();
        }

        super.onDestroy();
    }
}