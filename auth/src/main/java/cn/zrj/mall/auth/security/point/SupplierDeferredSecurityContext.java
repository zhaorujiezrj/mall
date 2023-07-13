//package cn.zrj.mall.auth.security.point;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.log.LogMessage;
//import org.springframework.security.core.context.DeferredSecurityContext;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolderStrategy;
//
//import java.util.function.Supplier;
//
///**
// * @description: SupplierDeferredSecurityContext
// * @author: zhaorujie
// * @date: 2023/7/12
// * @version: v1.0
// **/
//@Slf4j
//public class SupplierDeferredSecurityContext implements DeferredSecurityContext {
//    private final Supplier<SecurityContext> supplier;
//
//    private final SecurityContextHolderStrategy strategy;
//
//    private SecurityContext securityContext;
//
//    private boolean missingContext;
//
//    public SupplierDeferredSecurityContext(Supplier<SecurityContext> supplier, SecurityContextHolderStrategy strategy) {
//        this.supplier = supplier;
//        this.strategy = strategy;
//    }
//
//    @Override
//    public SecurityContext get() {
//        init();
//        return this.securityContext;
//    }
//
//    @Override
//    public boolean isGenerated() {
//        init();
//        return this.missingContext;
//    }
//
//    private void init() {
//        if (this.securityContext != null) {
//            return;
//        }
//
//        this.securityContext = this.supplier.get();
//        this.missingContext = (this.securityContext == null);
//        if (this.missingContext) {
//            this.securityContext = this.strategy.createEmptyContext();
//            if (log.isTraceEnabled()) {
//                log.trace(String.valueOf(LogMessage.format("Created %s", this.securityContext)));
//            }
//        }
//    }
//}
