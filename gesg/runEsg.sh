
./runEsg.sh -t 2021-06-30 -j ALL
./runEsg.sh -t 2021-06-30 -j CIR
./runEsg.sh -t 2021-06-30


#########################################

export LANG=ko_KR.euckr
ESG_HOME="/app/qcm/gesg"
ESG_LIB=${ESG_HOME}/lib/gesg.jar
ESG_PROPS=${ESG_HOME}/properties/gesg.properties

ESG_ARG_J="ALL"

#########################################

# echo "[[ ==========> ${0} started. <========= ]]"

while [ "$*" != "" ]; do
    _key=; _val=

    if [[ "$1" == -* ]]; then
        _key=$1; shift

        while [ "$*" != "" ]; do
            if [[ "$1" == -* ]]; then
                break
            else
                _val="$_val $1"
                shift
            fi
        done
        #echo "key:$_key  val:$_val -> [0]:${_val[0]}"
        val_arr=(${_val})
        #echo "key:$_key  val[0]:${val_arr[0]}"
    else
        echo "[Info]: skip argument: $1"
        shift; continue
    fi

    case $_key in
        -t)
            if [[ ${val_arr[0]} =~ ^[0-9]{8}$ ]]; then
                ESG_ARG_T=$(date -d ${val_arr[0]} +'%Y-%m-%d')
                ret=$?
                if [ $ret -ne 0 ]; then
                    echo "Error: As-Of-Date invalid date format .... ${val_arr[0]}"
                    exit 1
                fi
            else
                ESG_ARG_T="${val_arr[0]}"
            fi
            ;;
        -j)
            if [ ${val_arr[0]} == "CIR" ]; then
                ESG_ARG_J="${val_arr[0]}"
            fi
            ;;
        *)
            ;;
    esac
done


#########################################

# echo "DATE: ${ESG_ARG_T}"
# echo "JOB: ${ESG_ARG_J}"
# java -Xms4096m -Xmx8096m -jar -Dlog4j.configurationFile=file:/app/qcm/gesg/properties/gesg_log4j2.xml /app/qcm/gesg/lib/gesg.jar -Dtime=2021-06-30 -Dproperties=/app/qcm/gesg/properties/gesg.properties -Djob=ALL

# nohup java -Xms4096m -Xmx8096m -jar -Dlog4j.configurationFile=file:${ESG_HOME}/properties/gesg_log4j2.xml ${ESG_LIB} -Dtime=${ESG_ARG_T} -Dproperties=${ESG_PROPS} -Djob=${ESG_ARG_J} -XX:-OmitStackTraceInFastThrow  >/dev/null 2>&1 &
java -Xms4096m -Xmx8096m -jar -Dlog4j.configurationFile=file:${ESG_HOME}/properties/gesg_log4j2.xml ${ESG_LIB} -Dtime=${ESG_ARG_T} -Dproperties=${ESG_PROPS} -Djob=${ESG_ARG_J} -XX:-OmitStackTraceInFastThrow >/dev/null 2>&1

#########################################




