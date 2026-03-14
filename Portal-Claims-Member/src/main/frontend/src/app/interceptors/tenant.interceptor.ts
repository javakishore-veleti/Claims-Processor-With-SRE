import { HttpInterceptorFn } from '@angular/common/http';

export const tenantInterceptor: HttpInterceptorFn = (req, next) => {
  const tenantId = localStorage.getItem('X-Tenant-Id') || 'default';
  const cloned = req.clone({
    setHeaders: {
      'X-Tenant-Id': tenantId
    }
  });
  return next(cloned);
};
