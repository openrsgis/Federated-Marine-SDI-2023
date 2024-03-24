class utils {
    constructor() {}  

    //根据产品名称返回产品类型
    getProductType(nameStr){
        if(nameStr.indexOf("_EO") != -1){
            return 'EO';
        }
        else if(nameStr.indexOf("_Vector") != -1){
            return 'Vector';
        }
    }
}

export default new utils();