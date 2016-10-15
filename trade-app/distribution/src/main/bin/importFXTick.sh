#!/bin/bash

#### environment variable ######
DATA_DIR=/home/share/fxdata
WORK_DIR=/home/share
DB_NAME=stock
################################

# currency pairs
CURRENCY_PAIRS="USDJPY EURJPY AUDJPY GBPJPY CHFJPY EURUSD GBPUSD AUDUSD USDCHF"
#CURRENCY_PAIRS="EURJPY AUDJPY"

# target yyyyMM
YEAR_MONTH="200905 200906 200907 200908 200909 200910 200911 200912 \
201001 201002 201003 201004 201005 201006 201007 201008 201009 201010 201011 201012 \
201101 201102 201103 201104 201105 201106 201107 201108 201109 201110 201111 201112 \
201201 201202 201203 201204 201205 201206 201207 201208 201209 201210 201211 201212 \
201301 201302 201303 201304 201305 201306 201307 201308 201309 201310 201311 201312 \
201401 201402 201403 201404 201405 201406 201407 201408 201409 201410 201411 201412 \
201501 201502 201503 201504 201505 201506 201507 201508 201509 201510 201511 201512 \
201601 201602 201603 201604 201605 201606 201607 201608 \
"
#YEAR_MONTH="200905 200906"

#echo $CURRENCY_PAIRS
#echo $YEAR_MONTH

# loop all year-month
for ym in $YEAR_MONTH
do
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
        # truncate table trn_fx_tick
        psql -U postgres -d $DB_NAME -w -c "truncate table trn_fx_tick;"
        # import all csv data into trn_fx_tick table.
        psql -U postgres -d $DB_NAME -w -c \
            "copy trn_fx_tick(currency_pair, regist_date, bid_price, ask_price) from '$csv_file' DELIMITER ',';"
        # update mid_price column.
        psql -U postgres -d $DB_NAME -w -c "update trn_fx_tick set mid_price = (bid_price + ask_price) / 2;"
        # launch candlestick data generation job.
        ./launch.sh "${currency}" "${ym}" >/dev/null 2>&1
        STATUS=$?
        if [ ! "$STATUS" = "0" ]; then
            echo "Error Occurred!" && exit $STATUS
        fi
        # truncate table trn_fx_tick.
        #psql -U postgres -d $DB_NAME -w -c "truncate table trn_fx_tick;"
        # remove csv file.
        rm $csv_file
    done
    vacuumdb -U postgres -d "$DB_NAME" -f
done
