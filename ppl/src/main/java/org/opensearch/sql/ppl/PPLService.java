package org.opensearch.sql.ppl;

import org.opensearch.sql.common.response.ResponseListener;
import org.opensearch.sql.ppl.domain.PPLQueryRequest;

import java.util.Optional;

public interface PPLService<QueryResponse,ExplainResponse,Plan> {
    void execute(PPLQueryRequest request, ResponseListener<QueryResponse> listener);

    void explain(PPLQueryRequest request, ResponseListener<ExplainResponse> listener);

    Plan plan(
            PPLQueryRequest request,
            Optional<ResponseListener<QueryResponse>> queryListener,
            Optional<ResponseListener<ExplainResponse>> explainListener);
}
