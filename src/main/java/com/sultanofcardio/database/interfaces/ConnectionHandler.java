package com.sultanofcardio.database.interfaces;

import java.sql.Connection;

public interface ConnectionHandler<R> {
    R handle(Connection connection);
}
