#!/user/bin/env groovy

def call(){

    def allowedTypes = ['test', 'uat', 'live']
    if (!params.development_type) {
        error "Parameter 'development_type' is not defined"
    }
    if (!(params.development_type in allowedTypes)) {
        error "Parameter 'development_type' must be one of: ${allowedTypes.join(', ')}"
    }
    echo "> development_type: ${params.development_type}"

}