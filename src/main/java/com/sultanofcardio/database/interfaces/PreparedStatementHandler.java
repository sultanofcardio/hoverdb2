package com.sultanofcardio.database.interfaces;

import java.sql.PreparedStatement;

public interface PreparedStatementHandler<R> {
    R handle(PreparedStatement preparedStatement);
}
