package com.sdp.appazul.activities.transactions;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sdp.appazul.R;
import com.sdp.appazul.classes.FilterTypes;
import com.sdp.appazul.globals.Constants;

import java.util.ArrayList;
import java.util.List;

public class ExtraFilterBottomMenu extends BottomSheetDialogFragment {

    ChipGroup chipGroup;
    ChipGroup statusChipGroup;
    String[] transactionsType = {"Venta", "Sólo autorización", "Autorización completada"};
    String[] statusData = {"Procesado", "Expirado", "Abierto", "Generado", "Cancelado", "Anulada"};
    RelativeLayout btnAddExtraFilters;
    ExtraFilterInterface extraFilterInterface;
    List<FilterTypes> trnTypeArray;
    List<FilterTypes> filterTypesList = new ArrayList<>();
    LinearLayout qrFormButton;

    public ExtraFilterBottomMenu() {
    }

    public ExtraFilterBottomMenu(List trnTypeArray) {
        this.trnTypeArray = trnTypeArray;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.extra_filter_layout, container, false);
        initControls(view);

        return view;

    }


    private void initControls(View view) {
        chipGroup = view.findViewById(R.id.chipGroup);
        statusChipGroup = view.findViewById(R.id.statusChipGroup);
        btnAddExtraFilters = view.findViewById(R.id.btnAddExtraFilters);
        qrFormButton = view.findViewById(R.id.qrFormButton);


        qrFormButton.setOnClickListener(view1 -> {
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                chip.setChecked(false);
            }
            for (int i = 0; i < statusChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) statusChipGroup.getChildAt(i);
                chip.setChecked(false);
            }
            btnAddExtraFilters.setVisibility(View.INVISIBLE);
            qrFormButton.setVisibility(View.GONE);
        });

        otherConfig();


        addChipsToGroup();

    }

    private void otherConfig() {
        btnAddExtraFilters.setOnClickListener(btnAddExtraFiltersView -> {
            for (int i = 0; i < chipGroup.getChildCount(); i++) {

                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (!filterTypesList.contains(chip.getText().toString())) {
                    filterTypesList.add(new FilterTypes(chip.getText().toString(), Constants.TRN_TYPE, Constants.BOOLEAN_FALSE));
                }
            }
            for (int i = 0; i < statusChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) statusChipGroup.getChildAt(i);
                if (!filterTypesList.contains(chip.getText().toString())) {
                    filterTypesList.add(new FilterTypes(chip.getText().toString(), Constants.TRN_STATUS, Constants.BOOLEAN_FALSE));
                }
            }


            chipGroupFilter(chipGroup);
            statusChipGroupFilter(statusChipGroup);


            extraFilterInterface.getSelectedFilters(filterTypesList);
            dismiss();


        });
    }

    private void chipGroupFilter(ChipGroup chipGroup) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {

            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                if (i < chipGroup.getChildCount() - 1) {
                    checkFilterNameMinus(filterTypesList, chip);
                } else {
                    checkFilterName(filterTypesList, chip);
                }
            }
        }
    }

    private void statusChipGroupFilter(ChipGroup statusChipGroup) {
        for (int i = 0; i < statusChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) statusChipGroup.getChildAt(i);
            if (chip.isChecked()) {
                if (i < statusChipGroup.getChildCount() - 1) {
                    checkFilterNameMinus(filterTypesList, chip);
                } else {
                    checkFilterName(filterTypesList, chip);
                }
            }
        }
    }

    private void checkFilterNameMinus(List<FilterTypes> filterTypesList, Chip chip) {
        for (int j = 0; j < filterTypesList.size(); j++) {
            if (filterTypesList.get(j).getFilterName().equalsIgnoreCase(chip.getText().toString())) {
                filterTypesList.get(j).setIsSelected("true");
            }
        }
    }

    private void checkFilterName(List<FilterTypes> filterTypesList, Chip chip) {
        for (int j = 0; j < filterTypesList.size(); j++) {
            if (filterTypesList.get(j).getFilterName().equalsIgnoreCase(chip.getText().toString())) {
                filterTypesList.get(j).setIsSelected("true");
            }
        }
    }


    private void addChipsToGroup() {
        if (trnTypeArray != null && trnTypeArray.size() > 0) {
            addChipWithSelectedItems(trnTypeArray);
        } else {
            addChipsWithNewItems();
        }

    }

    private void addChipsWithNewItems() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        for (String chipData : transactionsType) {
            Chip chip = (Chip) layoutInflater.inflate(R.layout.other_filter_chip_item, null, false);
            chip.setText(chipData);
            chip.setCloseIconVisible(false);
            chip.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.unlink_background));
            Typeface typeface;
            typeface = Typeface.createFromAsset(getActivity().getAssets(), Constants.FONT_MONTSERRAT_SEMI_BOLD);
            chip.setTypeface(typeface);

            chip.setOnCheckedChangeListener((compoundButton, b) -> {
                if (chipGroup.getCheckedChipIds().size() > 0 || statusChipGroup.getCheckedChipIds().size() > 0) {
                    qrFormButton.setVisibility(View.VISIBLE);
                    btnAddExtraFilters.setVisibility(View.VISIBLE);
                } else {
                    qrFormButton.setVisibility(View.GONE);
                    btnAddExtraFilters.setVisibility(View.INVISIBLE);
                }

            });
            chipGroup.addView(chip);

        }
        for (String chipStatusData : statusData) {
            Chip chip = (Chip) layoutInflater.inflate(R.layout.other_filter_chip_item, null, false);
            chip.setText(chipStatusData);
            chip.setCloseIconVisible(false);
            chip.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.unlink_background));
            Typeface typeface;
            typeface = Typeface.createFromAsset(getActivity().getAssets(), Constants.FONT_MONTSERRAT_SEMI_BOLD);
            chip.setTypeface(typeface);
            chip.setOnCheckedChangeListener((compoundButton, b) -> {
                if (chipGroup.getCheckedChipIds().size() > 0 || statusChipGroup.getCheckedChipIds().size() > 0) {
                    qrFormButton.setVisibility(View.VISIBLE);
                    btnAddExtraFilters.setVisibility(View.VISIBLE);
                } else {
                    qrFormButton.setVisibility(View.GONE);
                    btnAddExtraFilters.setVisibility(View.INVISIBLE);
                }

            });
            statusChipGroup.addView(chip);
        }
    }


    private void addChipWithSelectedItems(List<FilterTypes> trnTypeArray) {

        for (FilterTypes selectedChipData : trnTypeArray) {
            callChipData(selectedChipData);
            statusDataCheck(selectedChipData, statusData);

        }
    }

    private void callChipData(FilterTypes selectedChipData) {
        for (String chipData : transactionsType) {
            if (selectedChipData != null
                    && selectedChipData.getFilterType().equalsIgnoreCase(Constants.TRN_TYPE)
                    && selectedChipData.getIsSelected() != null) {
                if (selectedChipData.getIsSelected().equalsIgnoreCase("true")
                        && !selectedChipData.getFilterName().equalsIgnoreCase("null")
                        && selectedChipData.getFilterName().equalsIgnoreCase(chipData)) {
                    selectedChipUi(chipData, 1);

                } else {
                    compareIsSelected(selectedChipData);
                    break;
                }
            }
        }
    }

    private void compareIsSelected(FilterTypes selectedChipData) {
        if (selectedChipData.getIsSelected().equalsIgnoreCase(Constants.BOOLEAN_FALSE)
                && !selectedChipData.getFilterName().equalsIgnoreCase("null")) {
            normalUi(selectedChipData.getFilterName(), 1);
        }
    }

    private void statusDataCheck(FilterTypes selectedChipData, String[] statusData) {
        for (String chipStatusData : statusData) {
            if (selectedChipData != null
                    && selectedChipData.getFilterType().equalsIgnoreCase(Constants.TRN_STATUS)
                    && selectedChipData.getIsSelected() != null) {

                if (selectedChipData.getIsSelected().equalsIgnoreCase("true")
                        && !selectedChipData.getFilterName().equalsIgnoreCase("null")
                        && selectedChipData.getFilterName().equalsIgnoreCase(chipStatusData)) {
                    selectedChipUi(selectedChipData.getFilterName(), 0);
                } else {
                    checkIsSelectedFlag(selectedChipData);
                    break;
                }
            }
        }
    }

    private void checkIsSelectedFlag(FilterTypes selectedChipData) {
        if (selectedChipData.getIsSelected().equalsIgnoreCase(Constants.BOOLEAN_FALSE)
                && !selectedChipData.getFilterName().equalsIgnoreCase("null")) {
            normalUi(selectedChipData.getFilterName(), 0);
        }
    }

    private void normalUi(String chipData, int dataType) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        Chip chip = (Chip) layoutInflater.inflate(R.layout.other_filter_chip_item, null, false);
        chip.setText(chipData);
        chip.setCloseIconVisible(false);
        Typeface typeface;
        typeface = Typeface.createFromAsset(getActivity().getAssets(), Constants.FONT_MONTSERRAT_SEMI_BOLD);
        chip.setTypeface(typeface);
        if (dataType == 1) {
            chipGroup.addView(chip);
        } else {
            statusChipGroup.addView(chip);
        }

    }

    private void selectedChipUi(String chipData, int dataType) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        if (chipData != null && !chipData.equalsIgnoreCase("null")) {
            Chip chip = (Chip) layoutInflater.inflate(R.layout.other_filter_chip_item, null, false);
            chip.setText(chipData);
            chip.setCloseIconVisible(false);
            chip.setChecked(true);
            chip.setCheckable(true);
            Typeface typeface;
            typeface = Typeface.createFromAsset(getActivity().getAssets(), Constants.FONT_MONTSERRAT_SEMI_BOLD);
            chip.setTypeface(typeface);
            if (dataType == 1) {
                chipGroup.addView(chip);
            } else {
                statusChipGroup.addView(chip);
            }

            if (chipGroup.getCheckedChipIds().size() > 0 || statusChipGroup.getCheckedChipIds().size() > 0) {
                qrFormButton.setVisibility(View.VISIBLE);
                btnAddExtraFilters.setVisibility(View.VISIBLE);
            } else {
                qrFormButton.setVisibility(View.GONE);
                btnAddExtraFilters.setVisibility(View.INVISIBLE);
            }
        }
    }


    public interface ExtraFilterInterface {
        void getSelectedFilters(List trnTypeFilters);
    }


    public void setExtraFilterInterface(ExtraFilterInterface extraFilterInterface) {
        this.extraFilterInterface = extraFilterInterface;
    }
}
