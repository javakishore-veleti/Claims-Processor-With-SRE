import { HttpInterceptorFn } from '@angular/common/http';

export const tenantInterceptor: HttpInterceptorFn = (req, next) => {
  const tenantId = localStorage.getItem('tenantId') || 'default-tenant';
  const cloned = req.clone({
    setHeaders: { 'X-Tenant-Id': tenantId }
  });
  return next(cloned);
};
