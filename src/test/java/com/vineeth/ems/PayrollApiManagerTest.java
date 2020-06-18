package com.vineeth.ems;

import com.vineeth.ems.api.PayrollApiManager;
import com.vineeth.ems.api.PayrollCreateRequest;
import com.vineeth.ems.api.PayrollCreateResponse;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;

public class PayrollApiManagerTest {
    @InjectMocks
    private PayrollApiManager payrollApiManager;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(payrollApiManager, "payrollHost", "http://foo.com");
    }

    @Test
    @Ignore
    public void testCreatePayrollAPIIfSuccess() throws Exception {
        PayrollCreateResponse payrollCreateResponse = new PayrollCreateResponse();
        payrollCreateResponse.setData(null);
        payrollCreateResponse.setStatus("success");
        ResponseEntity<PayrollCreateResponse> responseEntity = new ResponseEntity<>(payrollCreateResponse, HttpStatus.OK);

        Mockito.when(restTemplate.exchange(
                Mockito.eq("http://foo.com/api/v1/create"),
                Mockito.eq(HttpMethod.POST),
                Mockito.<HttpEntity<PayrollCreateRequest>>any(),
                Mockito.<Class<PayrollCreateResponse>>any())
        ).thenReturn(responseEntity);

        payrollApiManager.createPayrollForEmployee("abc", 123, 456);

        ArgumentCaptor<HttpEntity<PayrollCreateRequest>> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        Mockito.verify(restTemplate).exchange("http://foo.com/api/v1/create", HttpMethod.POST,
                httpEntityArgumentCaptor.capture(), PayrollCreateResponse.class);

        PayrollCreateRequest payrollCreateRequest = httpEntityArgumentCaptor.getValue().getBody();
        Assertions.assertEquals("abc", payrollCreateRequest.getName());
        Assertions.assertEquals("123", payrollCreateRequest.getSalary());
        Assertions.assertEquals("456", payrollCreateRequest.getAge());
    }
}
