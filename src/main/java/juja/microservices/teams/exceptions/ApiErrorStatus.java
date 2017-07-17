package juja.microservices.teams.exceptions;

public enum ApiErrorStatus {

    USER_NOT_IN_TEAM_EXCEPTION(
            "TMF-F2-D2",
            "You cannot deactivate team if user not in team",
            "The reason of the exception is that user not in team"
    ),

    USER_IN_SEVERAL_TEAMS_EXCEPTION(
            "TMF-F2-D2",
            "You cannot deactivate team if user in several teams",
            "The reason of the exception is that user in several teams"
    ),

    TEAMS_EXCEPTION(
            "0",
            "Oops something went wrong :(",
            "The exception is general in team microservice"
    ),
    USER_NOT_KEEPER_EXCEPTION(
            "TMF-F1-D1",
            "Sorry, but you're not a keeper",
            "Exception - NotKeeperException"
    ),

    USER_EXISTS_EXCEPTION(
            "TMF-F1-D3",
            "Sorry, but the user exists in a other team",
            "Exception - UserExistsException"
    ),

    OTHER_EXCEPTION(
            "2",
            "Oops something went wrong :(",
            "For this exception we have no comment :("
    ),

    SPRING_EXCEPTION(
            "100",
            "Oops something went wrong :(",
            "The exception is internal Spring exceptions"
    ),

    SPRING_NOT_VALID_REQUEST_EXCEPTION(
            "101",
            "You request is not valid",
            "The exception is Spring exceptions: BindException or MethodArgumentNotValidException"
    ),

    SPRING_REQUEST_PARAMETER_NOT_FOUND_EXCEPTION(
            "102",
            "In your request is missing parameters",
            "The exception is Spring exceptions: MissingServletRequestPartException or MissingServletRequestParameterException"
    ),

    SPRING_TYPE_MISMATCH(
            "103",
            "Type mismatch",
            "This exception is thrown when try to set bean property with wrong type. Exception - TypeMismatchException"
    ),
    SPRING_HTTP_MEDIA_TYPE_NOT_SUPPORTED(
            "104",
            "Unsupported media type",
            "The client send a request with unsupported media type. Exception - HttpMediaTypeNotSupportedException"
    ),
    SPRING_HTTP_REQUEST_METHOD_NOT_SUPPORTED(
            "105",
            "Unsupported HTTP method",
            "You send a requested with an unsupported HTTP method. Exception - HttpRequestMethodNotSupportedException"
    ),
    SPRING_HTTP_MEDIA_TYPE_NOT_ACCEPTABLE(
            "106",
            "Not acceptable media type",
            "The client send a request with not acceptable media type. Exception - HttpMediaTypeNotAcceptableException"
    ),
    SPRING_PATH_VARIABLE_NOT_FOUND_EXCEPTION(
            "107",
            "In your request is missing variables",
            "The exception is Spring exceptions: MissingPathVariableException"
    ),
    SPRING_NO_HANDLER_FOUND_EXCEPTION(
            "108",
            "404 response",
            "The exception is Spring exceptions: NoHandlerFoundException"
    );

    private String developerMessage;
    private String clientMessage;
    private String internalCode;

    ApiErrorStatus(String internalCode, String clientMessage, String developerMessage) {
        this.internalCode = internalCode;
        this.clientMessage = clientMessage;
        this.developerMessage = developerMessage;
    }

    public String internalCode() {
        return internalCode;
    }

    public String clientMessage() {
        return clientMessage;
    }

    public String developerMessage() {
        return developerMessage;
    }
}