#!/bin/bash

#### environment variable ######
DATA_DIR=/private/share
WORK_DIR=/usr/local/work
DB_NAME=dev_stock
################################

# currency pairs
CURRENCY_PAIRS=(USDJPY EURJPY AUDJPY GBPJPY CHFJPY EURUSD GBPUSD AUDUSD USDCHF)

# target yyyyMM
ym=$1

# loop all currency pairs
for currency in $CURRENCY_PAIRS
do
    # target zip file name.
    zip_file="${DATA_DIR}/${ym:0:4}/${currency}-${ym:0:4}-${ym:4:2}.zip"
    # extract the zip file to work directory.
    unzip $zip_file -d "${WORK_DIR}/"
    # target csv file name.
    csv_file="${WORK_DIR}/${currency}-${ym:0:4}-${ym:4:2}.csv"
    # patch the csv file(1.remove / in the currency pair name, 2.lower case currency pair name)
    awk '{ sub("/", "", $0); $0=tolower($0); print $0}' $csv_file > $csv_file.edit && \
        mv $csv_file.edit $csv_file
    # import all csv data into trn_fx_tick table.
    psql -U postgres -d $DB_NAME -w -c \
        "copy trn_fx_tick(currency_pair, regist_date, bid_price, ask_price) from '$f' DELIMITER ',';"
    # update mid_price column.
    psql -U postgres -d $DB_NAME -w -c "update trn_fx_tick set mid_price = (bid_price + ask_price) / 2;"
    # launch candlestick data generation job.
    ./launch.sh "${currency}" "${ym}"
    # truncate table trn_fx_tick.
    psql -U postgres -d $DB_NAME -w -c "truncate table trn_fx_tick;"
    # remove csv file.
    rm $csv_file
done
