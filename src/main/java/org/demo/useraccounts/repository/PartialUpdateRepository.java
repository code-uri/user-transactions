package org.demo.useraccounts.repository;

import java.util.Map;

public interface PartialUpdateRepository <T, ID>{



    T partialUpdate(ID id, Map<String, Object> updates);

}
