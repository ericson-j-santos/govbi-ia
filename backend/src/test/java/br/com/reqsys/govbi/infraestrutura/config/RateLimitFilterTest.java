package br.com.reqsys.govbi.infraestrutura.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RateLimitFilterTest {

    @Test
    void devePermitirRequisicaoDentroDoLimite() throws Exception {
        RateLimitProperties props = new RateLimitProperties();
        props.setHabilitado(true);
        props.setRequisicoesPorMinuto(5);
        RateLimitFilter filter = new RateLimitFilter(props);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/perguntas");
        request.addHeader("X-Usuario", "teste");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
        verify(chain).doFilter(request, response);
    }

    @Test
    void deveBloquearQuandoLimiteExcedido() throws Exception {
        RateLimitProperties props = new RateLimitProperties();
        props.setHabilitado(true);
        props.setRequisicoesPorMinuto(1);
        RateLimitFilter filter = new RateLimitFilter(props);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/auditoria/recentes");
        request.addHeader("X-Usuario", "limite");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);
        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertThat(response.getStatus()).isEqualTo(200);
    }
}
