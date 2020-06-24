package com.sultanofcardio.database.interfaces;

import com.sultanofcardio.database.Database;

public interface DatabaseHandler<D extends Database<?>, R> {
    R handle(D database);
}
