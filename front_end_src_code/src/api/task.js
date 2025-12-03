import api from "./index.js";

export const LoadTaskList = () =>{
  return api.get("/task/loadTaskList")
}

export const loadDataView = () =>{
   return api.get("/task/loadDataView")
}

export const loadHeatData = () =>{
  return api.get("/task/loadHeatData")
}

export const newTask = (data) =>{
  return api.post("/task/newTask",data)
}

export const loadDetailTask = (taskId) =>{
  return api.get("/task/loadDetailTask",
    {params: {id: taskId}}
  )
}

export const loadTeamMembers = (taskId) =>{
  return api.get("/task/loadTeamMembers",
    {params: {id: taskId}}
  )
}

export const loadTeamProcess = (taskId) =>{
  return api.get("/task/loadTeamProcess",
    {params: {id: taskId}}
  )
}
