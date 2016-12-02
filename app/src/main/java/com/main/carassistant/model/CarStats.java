package com.main.carassistant.model;

public class CarStats {

    private int mileage;
    private int fueling;
    private int oil;

    private CarStats(final int pMileage, final int pFueling, final int pOil) {
        mileage = pMileage;
        fueling = pFueling;
        oil = pOil;
    }

    public int getMileage() {
        return mileage;
    }

    public int getFueling() {
        return fueling;
    }

    public int getOil() {
        return oil;
    }

    private CarStats() {
    }

    public static class Builder {

        private int mileage;
        private int fueling;
        private int oil;

        public Builder setMileage(int mileage) {
            this.mileage = mileage;
            return this;
        }

        public Builder setFueling(int fueling) {
            this.fueling = fueling;
            return this;
        }

        public Builder setOil(int oil) {
            this.oil = oil;
            return this;
        }

        public CarStats build() {
            return new CarStats(mileage, fueling, oil);
        }
    }
}
