export interface PaginationInfo<T> {
  pageable: boolean;
  pageNumber: number;
  pageSize: number;
  dataList: T[];
  total: number;
  first: boolean;
  last: boolean;
  empty: boolean;
  totalPages: number;
}
